package com.lucasmaciel404.pdv_api.controller;

import com.lucasmaciel404.pdv_api.model.UserModel;
import com.lucasmaciel404.pdv_api.dto.request.LoginUserRequest;
import com.lucasmaciel404.pdv_api.dto.response.LoginUserResponse;
import com.lucasmaciel404.pdv_api.security.JwtUtil;
import com.lucasmaciel404.pdv_api.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginUserRequest request) {

        Optional<UserModel> userOpt = userService.findByEmail(request.email());

        if (userOpt.isEmpty()) {
            return ResponseEntity.status(401).body("Credenciais inválidas");
        }

        UserModel user = userOpt.get();

        if (!bCryptPasswordEncoder.matches(request.password(), user.getPassword())) {
            return ResponseEntity.status(401).body("Credenciais inválidas");
        }

        String token = jwtUtil.generateToken(user.getEmail());

        return ResponseEntity.ok(new LoginUserResponse(token));
    }
}
