package com.lucasmaciel404.pdv_api.dto.response;

import com.lucasmaciel404.pdv_api.model.OrderItemsModel;
import com.lucasmaciel404.pdv_api.model.OrderModel;
import com.lucasmaciel404.pdv_api.model.TableModel;

import java.util.List;

public record GetOrderByCardIdResponse(
        String orderId,
        String status,
        Integer tableNumber,
        List<OrderItemResponse> items
) { }
