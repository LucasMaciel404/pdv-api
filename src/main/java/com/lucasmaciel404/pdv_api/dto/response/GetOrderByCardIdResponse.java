package com.lucasmaciel404.pdv_api.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record GetOrderByCardIdResponse(
        String orderId,
        String status,
        Integer tableNumber,
        List<OrderItemResponse> items,
        BigDecimal subtotalAmount,
        BigDecimal serviceFeePercentage,
        BigDecimal serviceFeeAmount,
        BigDecimal totalAmount,
        LocalDateTime createdAt,
        LocalDateTime closedAt
) {}