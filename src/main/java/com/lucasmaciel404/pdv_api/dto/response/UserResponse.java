package com.lucasmaciel404.pdv_api.dto.response;

import com.lucasmaciel404.pdv_api.dto.enums.model.UserRoleEnum;

import java.time.LocalDateTime;
import java.util.UUID;

public record UserResponse(
        UUID id,
        String name,
        String email,
        UserRoleEnum role,
        String stripeCustomerId,
        String subscriptionId,
        Boolean subscriptionActive,
        LocalDateTime subscriptionDate
) {
}
