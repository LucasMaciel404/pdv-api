package com.lucasmaciel404.pdv_api.dto.response;

import java.math.BigDecimal;

public record OrderItemResponse(
        String id,
        Integer quantity,
        BigDecimal unitPrice,
        ProductSummaryResponse product
) { }
