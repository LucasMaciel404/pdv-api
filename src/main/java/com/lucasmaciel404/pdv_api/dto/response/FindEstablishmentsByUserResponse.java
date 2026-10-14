package com.lucasmaciel404.pdv_api.dto.response;

import com.lucasmaciel404.pdv_api.dto.enums.model.UserRoleEnum;

import java.time.LocalDateTime;
import java.util.UUID;

public record FindEstablishmentsByUserResponse(
        UUID id,
        String name,
        Boolean active,
        LocalDateTime createdAt,
        UserRoleEnum role
) {}