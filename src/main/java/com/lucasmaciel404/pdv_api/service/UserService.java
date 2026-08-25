package com.lucasmaciel404.pdv_api.service;

import com.lucasmaciel404.pdv_api.dto.ForgotPasswordRequestDTO;
import com.lucasmaciel404.pdv_api.dto.LoginResponseDTO;
import com.lucasmaciel404.pdv_api.dto.ResetPasswordRequestDTO;
import com.lucasmaciel404.pdv_api.dto.mapper.UserMapper;
import com.lucasmaciel404.pdv_api.dto.request.LoginUserRequest;
import com.lucasmaciel404.pdv_api.dto.response.UserResponse;
import com.lucasmaciel404.pdv_api.model.PasswordResetTokenModel;
import com.lucasmaciel404.pdv_api.model.UserModel;
import com.lucasmaciel404.pdv_api.repository.PasswordResetTokenRepository;
import com.lucasmaciel404.pdv_api.repository.UserRepository;
import com.lucasmaciel404.pdv_api.dto.request.RegisterUserRequest;
import com.lucasmaciel404.pdv_api.dto.response.RegisterUserResponse;
import com.lucasmaciel404.pdv_api.security.jwt.JwtUtil;
import com.stripe.model.Customer;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final UserMapper userMapper;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final EmailService emailService;

    public RegisterUserResponse registerUser(RegisterUserRequest request) {

        if (userRepository.existsByEmail(request.email())) {
            throw new RuntimeException("User already exists");
        }

        String customerId = this.createCustomer(request.email(), request.name());

        UserModel user = new UserModel();
        user.setName(request.name());
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setRole(request.role());
        user.setStripeCustomerId(customerId);

        UserModel savedUser = userRepository.save(user);

        return new RegisterUserResponse(
                savedUser.getId(),
                savedUser.getName(),
                savedUser.getEmail(),
                savedUser.getRole(),
                savedUser.getActive(),
                savedUser.getCreatedAt()
        );
    }

    public LoginResponseDTO login(LoginUserRequest request) {

        UserModel user = userRepository.findByEmail(request.email())
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.UNAUTHORIZED)
                );

        if (!bCryptPasswordEncoder.matches(
                request.password(),
                user.getPassword()
        )) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }

        String token = jwtUtil.generateToken(user.getEmail());

        UserResponse userResponse = userMapper.toResponse(user);

        return new LoginResponseDTO(
                token,
                userResponse
        );
    }

    public void forgotPassword(ForgotPasswordRequestDTO request) {

        UserModel user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        String token = UUID.randomUUID().toString();

        PasswordResetTokenModel resetToken = new PasswordResetTokenModel();
        resetToken.setToken(token);
        resetToken.setUser(user);
        resetToken.setExpiresAt(LocalDateTime.now().plusMinutes(30));
        resetToken.setUsed(false);

        passwordResetTokenRepository.save(resetToken);

        emailService.sendPasswordResetEmail(
                user.getEmail(),
                token
        );
    }

    public void resetPassword(ResetPasswordRequestDTO request) {

        PasswordResetTokenModel resetToken =
                passwordResetTokenRepository.findByToken(request.token())
                        .orElseThrow(() ->
                                new ResponseStatusException(HttpStatus.BAD_REQUEST,"Token inválido")
                        );

        if (resetToken.isUsed()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Token já utilizado"
            );
        }

        if (resetToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Token expirado"
            );
        }

        UserModel user = resetToken.getUser();

        user.setPassword( passwordEncoder.encode(request.newPassword()) );

        userRepository.save(user);

        resetToken.setUsed(true);
        passwordResetTokenRepository.save(resetToken);
    }

    public String createCustomer(String email, String name) {
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("email", email);
            params.put("name", name);

            Customer customer = Customer.create(params);

            return customer.getId();

        } catch (Exception e) {
            throw new RuntimeException("Erro ao criar customer no Stripe", e);
        }
    }

    public Optional<UserModel> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}
