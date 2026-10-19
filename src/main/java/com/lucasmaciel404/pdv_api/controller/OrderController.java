package com.lucasmaciel404.pdv_api.controller;

import com.lucasmaciel404.pdv_api.dto.request.AddItemsRequest;
import com.lucasmaciel404.pdv_api.dto.response.CreateOrderRequest;
import com.lucasmaciel404.pdv_api.dto.response.CreateOrderResponse;
import com.lucasmaciel404.pdv_api.dto.response.GetOrderByCardIdResponse;
import com.lucasmaciel404.pdv_api.model.OrderModel;
import com.lucasmaciel404.pdv_api.service.OrderService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @GetMapping("/by-card/{cardCode}")
    public ResponseEntity<GetOrderByCardIdResponse> getOrderByCardCode(
            @PathVariable String cardCode
    ) {
        GetOrderByCardIdResponse response =
                orderService.getOrderByCardId(cardCode);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/by-card/{cardCode}")
    public ResponseEntity<CreateOrderResponse> createOrder(
            @PathVariable String cardCode,
            @RequestBody CreateOrderRequest request
    ) {
        CreateOrderResponse response =
                orderService.createOrderByCardId(
                        cardCode,
                        request.table()
                );

        return ResponseEntity.status(201).body(response);
    }

    @PostMapping("/items")
    public ResponseEntity<OrderModel> addItems(
            @RequestBody AddItemsRequest request
    ) {
        OrderModel response = orderService.addItemsToOrder(
                request.cardCode(),
                request.tableId(),
                request.items()
        );

        return ResponseEntity.ok(response);
    }

    @PostMapping("/close/{orderId}")
    public ResponseEntity<Void> closeOrder(
            @PathVariable UUID orderId
    ) {
        orderService.closeByOrderId(orderId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<Void> deleteItems(
            @PathVariable UUID itemId
    ) {
        orderService.deleteItemById(itemId);

        return ResponseEntity.noContent().build();
    }
}