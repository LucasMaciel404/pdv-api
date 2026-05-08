package com.lucasmaciel404.pdv_api.controller;

import com.lucasmaciel404.pdv_api.dto.request.RegisterUserRequest;
import com.lucasmaciel404.pdv_api.dto.response.RegisterUserResponse;
import com.lucasmaciel404.pdv_api.dto.request.LoginUserRequest;
import com.lucasmaciel404.pdv_api.security.JwtUtil;
import com.lucasmaciel404.pdv_api.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JwtUtil jwtUtil;

    @PostMapping("/register")
    public ResponseEntity<RegisterUserResponse> registerUser(
            @RequestBody RegisterUserRequest request
    ) {
        RegisterUserResponse response = userService.registerUser(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    @PostMapping("/login")
    public ResponseEntity<ResponseEntity<?>> loginUser(
            @RequestBody LoginUserRequest request
    ) {
        ResponseEntity<?> response = userService.login(request);

        return ResponseEntity.ok(response);
    }
}