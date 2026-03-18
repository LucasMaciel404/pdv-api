package com.lucasmaciel404.pdv_api.controller;

import com.lucasmaciel404.pdv_api.dto.request.AddItemsRequest;
import com.lucasmaciel404.pdv_api.dto.response.CreateOrderRequest;
import com.lucasmaciel404.pdv_api.dto.response.GetOrderByCardIdResponse;
import com.lucasmaciel404.pdv_api.model.OrderModel;
import com.lucasmaciel404.pdv_api.service.OrderService;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @GetMapping("/by-card/{cardCode}")
    public ResponseEntity<?> getOrderByCardCode(@PathVariable String cardCode) {
        try {
            GetOrderByCardIdResponse response = orderService.getOrderByCardId(cardCode);
            return ResponseEntity.ok(response);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
    @PostMapping("/by-card/{cardCode}")
    public ResponseEntity<?> createOrder(@PathVariable String cardCode, @RequestBody CreateOrderRequest request) {
        try{
            OrderModel response = orderService.createOrderByCardId(cardCode, request.table());
            return ResponseEntity.ok(response);
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @PostMapping("/items/{cardId}")
    public ResponseEntity<?> addItems(@PathVariable String cardId, @RequestBody AddItemsRequest request) {
        try{
            OrderModel response = orderService.addItemsToOrder(cardId, request.items());
            return ResponseEntity.ok(response);
        } catch ( Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/close/{cardCode}")
    public ResponseEntity<?> closeOrder(@PathVariable String cardCode) {
        orderService.closeByCardCode(cardCode);
        return ResponseEntity.ok().build();
    }
    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<?> deleteItems(@PathVariable UUID itemId) {
        orderService.deleteItemById(itemId);
        return ResponseEntity.ok().build();
    }
}
