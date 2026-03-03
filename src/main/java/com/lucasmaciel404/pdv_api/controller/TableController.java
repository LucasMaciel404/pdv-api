package com.lucasmaciel404.pdv_api.controller;

import com.lucasmaciel404.pdv_api.dto.request.CreateTableRequest;
import com.lucasmaciel404.pdv_api.model.TableModel;
import com.lucasmaciel404.pdv_api.service.TableService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@RestController
@RequestMapping("/tables")
@RequiredArgsConstructor
public class TableController {

    private final TableService service;

    @GetMapping("/by-card/{cardId}")
    public ResponseEntity<?> findByCardId(@PathVariable UUID cardId) {
        return ResponseEntity.ok(service.findByCardId(cardId));
    }

    @GetMapping
    public ResponseEntity<?> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @PostMapping
    public ResponseEntity<?>  create(@RequestParam Integer number,
                                     @RequestParam(required = false) UUID cardId) {
        return ResponseEntity.ok(service.create(number, cardId));
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        service.delete(id);
    }
}
