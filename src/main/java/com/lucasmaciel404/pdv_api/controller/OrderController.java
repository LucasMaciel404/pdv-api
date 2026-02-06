package com.lucasmaciel404.pdv_api.controller;

import com.lucasmaciel404.pdv_api.dto.response.GetOrderByCardIdResponse;
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

    @GetMapping("/by-card/{cardUid}")
    public ResponseEntity<?> getOrderByCardUid(@PathVariable UUID cardUid) {
        try {
            GetOrderByCardIdResponse response = orderService.getOrderByCardId(cardUid);
            return ResponseEntity.ok(response);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
