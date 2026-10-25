package com.lucasmaciel404.pdv_api.dto.response;

import java.math.BigDecimal;
import java.util.List;

public record DashboardResponse(
        BigDecimal revenue,
        long completedOrders,
        BigDecimal averageTicket,
        List<SaleByDay> salesByDay,
        List<TopProduct> topProducts
) {
    public record SaleByDay(
            String date,
            BigDecimal revenue
    ) {}

    public record TopProduct(
            String productId,
            String name,
            long quantity,
            BigDecimal revenue
    ) {}
}
