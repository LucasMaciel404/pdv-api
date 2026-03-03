package com.lucasmaciel404.pdv_api.controller;

import com.lucasmaciel404.pdv_api.dto.request.AddItemsRequest;
import com.lucasmaciel404.pdv_api.dto.response.CreateOrderRequest;
import com.lucasmaciel404.pdv_api.dto.response.GetOrderByCardIdResponse;
import com.lucasmaciel404.pdv_api.model.OrderModel;
import com.lucasmaciel404.pdv_api.service.OrderService;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @GetMapping("/by-card/{cardId}")
    public ResponseEntity<?> getOrderByCardUid(@PathVariable UUID cardId) {
        try {
            GetOrderByCardIdResponse response = orderService.getOrderByCardId(cardId);
            return ResponseEntity.ok(response);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
    @PostMapping("/by-card/{cardId}")
    public ResponseEntity<?> createOrder(@PathVariable UUID cardId, @RequestBody CreateOrderRequest request) {
        try{
            OrderModel response = orderService.createOrderByCardId(cardId, request.tableId());
            return ResponseEntity.ok(response);
        }catch (Exception e){
            return ResponseEntity.badRequest().build();
        }
    }
    @PostMapping("/items/{cardId}")
    public ResponseEntity<?> addItems(@PathVariable UUID cardId, @RequestBody AddItemsRequest request) {
        try{
            OrderModel response = orderService.addItemsToOrder(cardId, request.items());
            return ResponseEntity.ok(response);
        } catch ( Exception e){
            return ResponseEntity.badRequest().build();
        }
    }
    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<?> deleteItems(@PathVariable UUID itemId) {
        orderService.deleteItemById(itemId);
        return ResponseEntity.ok().build();
    }
}
