package com.lucasmaciel404.pdv_api.dto.mapper;

import com.lucasmaciel404.pdv_api.dto.response.OrderItemResponse;
import com.lucasmaciel404.pdv_api.dto.response.ProductSummaryResponse;
import com.lucasmaciel404.pdv_api.model.OrderItemsModel;

public class OrderItemMapper {

    public static OrderItemResponse toResponse(OrderItemsModel itemModel){
        return new OrderItemResponse(
                itemModel.getId(),
                itemModel.getQuantity(),
                itemModel.getUnitPrice(),
                new ProductSummaryResponse(
                        itemModel.getProduct().getId(),
                         itemModel.getProduct().getName(),
                        itemModel.getProduct().getPrice()
                )
        );
    }
}
