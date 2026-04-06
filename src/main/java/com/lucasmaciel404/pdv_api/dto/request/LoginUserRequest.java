package com.lucasmaciel404.pdv_api.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginUserRequest(
        @Email
        @NotBlank
        String email,

        @NotBlank
        String password
) {}
