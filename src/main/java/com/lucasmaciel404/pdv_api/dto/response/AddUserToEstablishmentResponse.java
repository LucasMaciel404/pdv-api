package com.lucasmaciel404.pdv_api.dto.response;

import com.lucasmaciel404.pdv_api.dto.enums.model.UserRoleEnum;

import java.util.UUID;

public record AddUserToEstablishmentResponse(
        UUID id,
        UUID userId,
        UUID establishmentId,
        UserRoleEnum role
) {}