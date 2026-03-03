package com.lucasmaciel404.pdv_api.dto.response;

import java.math.BigDecimal;
import java.util.UUID;

public record ProductSummaryResponse(
        UUID id,
        String name,
        BigDecimal unitPrice
) { }
