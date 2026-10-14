package com.lucasmaciel404.pdv_api.dto.mapper;

import com.lucasmaciel404.pdv_api.dto.response.CreateOrderResponse;
import com.lucasmaciel404.pdv_api.model.OrderModel;

public class OrderMapper {

    private OrderMapper() {
    }

    public static CreateOrderResponse toCreateResponse(OrderModel order) {
        return new CreateOrderResponse(
                order.getId(),
                order.getStatus().name(),
                order.getTable().getNumber()
        );
    }
}