package com.lucasmaciel404.pdv_api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record ProductRequest(

        @NotBlank
        String name,

        @NotNull
        @Positive
        BigDecimal price

) {}
