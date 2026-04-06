package com.lucasmaciel404.pdv_api.controller;

import com.lucasmaciel404.pdv_api.dto.request.CardRequest;
import com.lucasmaciel404.pdv_api.model.CardModel;
import com.lucasmaciel404.pdv_api.service.CardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/card")
public class CardController {
    private final CardService cardService;

    @PostMapping
    public ResponseEntity<?> createCard(@RequestBody CardRequest card) {
        try {
            CardModel response = cardService.createCard(card);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
          System.out.println(e);
          return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @GetMapping
    public ResponseEntity<?> getByCardCode(@PathVariable String cardCode) {
        CardModel response = cardService.getCardWithCardCode(cardCode);
        return ResponseEntity.ok(response);
    }
}
