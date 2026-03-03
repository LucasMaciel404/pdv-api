package com.lucasmaciel404.pdv_api.dto;

import java.util.UUID;

public record PedidoDTO(
        UUID productId,
        Integer quantity
) {
}
