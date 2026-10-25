r
package com.lucasmaciel404.pdv_api.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record UpdateTableRequest(
        @NotNull
        @Min(1)
        Integer number
) {}