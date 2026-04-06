package com.lucasmaciel404.pdv_api.controller;

import com.lucasmaciel404.pdv_api.dto.CheckoutRequestDTO;
import com.lucasmaciel404.pdv_api.model.UserModel;
import com.lucasmaciel404.pdv_api.service.StripeService;
import com.lucasmaciel404.pdv_api.service.UserService;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/stripe")
@RequiredArgsConstructor
public class StripeController {

    private final StripeService stripeService;

    @PostMapping("/checkout")
    public Map<String, String> createCheckout( @RequestBody CheckoutRequestDTO request, Authentication authentication) {
        String email = authentication.getName();

        String sessionId = stripeService.createCheckoutSession(email, request.priceId());

        return Map.of("url", sessionId);
    }
}