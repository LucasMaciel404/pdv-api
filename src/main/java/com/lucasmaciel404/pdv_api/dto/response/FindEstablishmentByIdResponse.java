package com.lucasmaciel404.pdv_api.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

public record FindEstablishmentByIdResponse(
        UUID id,
        String name,
        Boolean active,
        LocalDateTime createdAt
) {}