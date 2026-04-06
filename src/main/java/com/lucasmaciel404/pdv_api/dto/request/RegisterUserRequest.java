package com.lucasmaciel404.pdv_api.dto.request;

import com.lucasmaciel404.pdv_api.dto.enums.model.UserRoleEnum;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.jetbrains.annotations.NotNull;

public record RegisterUserRequest(
        @NotBlank
        @Size(max = 100)
        String name,

        @NotBlank
        @Email
        @Size(max = 100)
        String email,

        @NotBlank
        @Size(min = 6)
        String password,

        @NotNull
        UserRoleEnum role
) {}