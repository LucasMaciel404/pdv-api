package com.lucasmaciel404.pdv_api.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CreateTableRequest(
        @NotNull
        @Min(1)
        Integer number,

        @NotNull
        UUID establishment
) {}