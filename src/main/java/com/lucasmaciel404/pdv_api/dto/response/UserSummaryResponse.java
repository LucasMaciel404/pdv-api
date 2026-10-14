package com.lucasmaciel404.pdv_api.dto.response;

import java.util.UUID;

public record UserSummaryResponse(
        UUID id,
        String name,
        String email
) {
}