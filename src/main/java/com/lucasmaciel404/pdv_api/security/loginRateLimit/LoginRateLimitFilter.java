package com.lucasmaciel404.pdv_api.security.loginRateLimit;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import com.lucasmaciel404.pdv_api.service.LoginRateLimitService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class LoginRateLimitFilter
        extends OncePerRequestFilter {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(LoginRateLimitFilter.class);

    private static final String LOGIN_PATH = "/auth/login";

    private final LoginRateLimitService rateLimitService;

    public LoginRateLimitFilter(
            LoginRateLimitService rateLimitService
    ) {
        this.rateLimitService = rateLimitService;
    }

    @Override
    protected boolean shouldNotFilter(
            HttpServletRequest request
    ) {
        boolean isLoginPath =
                LOGIN_PATH.equals(request.getServletPath());

        boolean isPost =
                HttpMethod.POST.matches(request.getMethod());

        return !isLoginPath || !isPost;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String clientIp = request.getRemoteAddr();

        try {
            RateLimitResult result =
                    rateLimitService.consume(clientIp);

            response.setHeader(
                    "X-RateLimit-Remaining",
                    String.valueOf(result.remainingAttempts())
            );

            if (!result.allowed()) {
                sendTooManyRequests(response, result);
                return;
            }
        } catch (DataAccessException exception) {
            LOGGER.error(
                    "Redis indisponível durante o rate limit do login",
                    exception
            );
        }

        filterChain.doFilter(request, response);
    }

    private void sendTooManyRequests(
            HttpServletResponse response,
            RateLimitResult result
    ) throws IOException {

        response.setStatus(
                HttpStatus.TOO_MANY_REQUESTS.value()
        );

        response.setContentType("application/json");
        response.setCharacterEncoding(
                StandardCharsets.UTF_8.name()
        );

        response.setHeader(
                "Retry-After",
                String.valueOf(result.retryAfterSeconds())
        );

        response.getWriter().write(
                """
                {
                  "status": 429,
                  "error": "Too Many Requests",
                  "message": "Muitas tentativas de login. Tente novamente em %d segundos."
                }
                """.formatted(result.retryAfterSeconds())
        );
    }
}