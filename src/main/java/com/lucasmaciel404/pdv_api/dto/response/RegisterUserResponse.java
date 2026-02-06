package com.lucasmaciel404.pdv_api.dto.response;


import com.lucasmaciel404.pdv_api.dto.enums.model.UserRoleEnum;

import java.time.LocalDateTime;
import java.util.UUID;

public record RegisterUserResponse(
        UUID id,
        String name,
        String email,
        UserRoleEnum role,
        Boolean active,
        LocalDateTime createdAt
) {}
