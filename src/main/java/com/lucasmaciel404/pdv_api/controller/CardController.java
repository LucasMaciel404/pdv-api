package com.lucasmaciel404.pdv_api.controller;

import com.lucasmaciel404.pdv_api.dto.request.CardRequest;
import com.lucasmaciel404.pdv_api.model.CardModel;
import com.lucasmaciel404.pdv_api.service.CardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.UUID;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/card")
public class CardController {

    private final CardService cardService;

    @GetMapping
    public ResponseEntity<List<CardModel>> getCards(
            Authentication authentication
    ) {
        return ResponseEntity.ok(
                cardService.getCardsByEstablishment(authentication.getName())
        );
    }

    @PostMapping
    public ResponseEntity<CardModel> createCard(
            @RequestBody CardRequest card,
            Authentication authentication
    ) {
        CardModel response = cardService.createCard(
                card,
                authentication.getName()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/{cardId}")
    public ResponseEntity<Void> deleteCard(
            @PathVariable UUID cardId,
            Authentication authentication
    ) {
        cardService.deleteCard(cardId, authentication.getName());
        return ResponseEntity.noContent().build();
    }
}