
package com.lucasmaciel404.pdv_api.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record ProductResponse(
        UUID id,
        String name,
        String description,
        String category,
        BigDecimal price,
        Boolean active,
        LocalDateTime createdAt,
        UUID establishmentId
) {}
