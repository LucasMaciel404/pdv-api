package com.lucasmaciel404.pdv_api.dto.request;

import com.lucasmaciel404.pdv_api.dto.PedidoDTO;

import java.util.List;
import java.util.UUID;

public record AddItemsRequest(
        List<PedidoDTO>  items
) {
}
