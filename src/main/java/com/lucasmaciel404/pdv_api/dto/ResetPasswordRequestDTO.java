package com.lucasmaciel404.pdv_api.dto;

public record ResetPasswordRequestDTO(
        String token,
        String newPassword
) {
}