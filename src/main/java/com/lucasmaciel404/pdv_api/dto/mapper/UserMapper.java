package com.lucasmaciel404.pdv_api.dto.mapper;

import com.lucasmaciel404.pdv_api.dto.response.UserResponse;
import com.lucasmaciel404.pdv_api.model.UserModel;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserResponse toResponse(UserModel user) {
        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole(),
                user.getStripeCustomerId(),
                user.getSubscriptionId(),
                user.getSubscriptionActive()
        );
    }
}