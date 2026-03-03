package com.lucasmaciel404.pdv_api.dto.response;

import com.lucasmaciel404.pdv_api.dto.enums.model.UserRoleEnum;

public record UserResponse(String name, String email, UserRoleEnum role ) {
}
