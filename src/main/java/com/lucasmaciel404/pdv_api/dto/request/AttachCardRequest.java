package com.lucasmaciel404.pdv_api.dto.request;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record AttachCardRequest(
        @NotNull UUID cardId
) {}