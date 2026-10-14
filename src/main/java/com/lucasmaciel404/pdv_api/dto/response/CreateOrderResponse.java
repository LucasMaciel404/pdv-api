package com.lucasmaciel404.pdv_api.dto.response;

import java.util.UUID;

public record CreateOrderResponse(
        UUID id,
        String status,
        Integer tableNumber
) {
}