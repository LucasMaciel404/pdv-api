package com.lucasmaciel404.pdv_api.service;

import com.lucasmaciel404.pdv_api.dto.LoginResponseDTO;
import com.lucasmaciel404.pdv_api.dto.mapper.UserMapper;
import com.lucasmaciel404.pdv_api.dto.request.LoginUserRequest;
import com.lucasmaciel404.pdv_api.dto.response.LoginUserResponse;
import com.lucasmaciel404.pdv_api.dto.response.UserResponse;
import com.lucasmaciel404.pdv_api.model.UserModel;
import com.lucasmaciel404.pdv_api.repository.UserRepository;
import com.lucasmaciel404.pdv_api.dto.request.RegisterUserRequest;
import com.lucasmaciel404.pdv_api.dto.response.RegisterUserResponse;
import com.lucasmaciel404.pdv_api.security.JwtUtil;
import com.stripe.model.Customer;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final UserMapper userMapper;

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
