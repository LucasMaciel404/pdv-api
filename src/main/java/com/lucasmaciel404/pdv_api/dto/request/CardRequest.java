package com.lucasmaciel404.pdv_api.dto.request;

import java.util.UUID;

public record CardRequest(String cardId, boolean active, UUID estabilishment) {

}
