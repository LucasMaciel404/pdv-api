package com.lucasmaciel404.pdv_api.dto.request;

import java.util.UUID;

public record CreateTableRequest(
        Integer number,
        UUID cardId
) {
}
