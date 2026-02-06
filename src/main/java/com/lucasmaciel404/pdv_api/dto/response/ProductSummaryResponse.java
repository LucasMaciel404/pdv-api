package com.lucasmaciel404.pdv_api.dto.response;

import java.math.BigDecimal;

public record ProductSummaryResponse(
        String id,
        String name,
        BigDecimal price
) { }
