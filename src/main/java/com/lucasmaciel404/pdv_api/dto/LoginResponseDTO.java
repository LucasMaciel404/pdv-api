package com.lucasmaciel404.pdv_api.dto;

import com.lucasmaciel404.pdv_api.dto.response.UserResponse;

public record LoginResponseDTO(
        String token,
        UserResponse user
) {}