package com.lucasmaciel404.pdv_api.dto.response;

import java.math.BigDecimal;
import java.util.UUID;

public record OrderItemResponse(
        UUID id,
        Integer quantity,
        BigDecimal price,
        ProductSummaryResponse product
) { }
