package com.lucasmaciel404.pdv_api.controller;

import com.lucasmaciel404.pdv_api.model.Establishment;
import com.lucasmaciel404.pdv_api.service.EstablishmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/establishments")
public class EstablishmentController {

    private final EstablishmentService establishmentService;

    // CREATE
    @PostMapping
    public ResponseEntity<Establishment> create(@RequestBody Establishment establishment) {
        Establishment created = establishmentService.create(establishment);
        return ResponseEntity.ok(created);
    }

    // FIND ALL
    @GetMapping
    public ResponseEntity<List<Establishment>> findAll() {
        return ResponseEntity.ok(establishmentService.findAll());
    }

    // FIND BY ID
    @GetMapping("/{id}")
    public ResponseEntity<Establishment> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(establishmentService.findById(id));
    }

    // UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<Establishment> update(
            @PathVariable UUID id,
            @RequestBody Establishment establishment
    ) {
        return ResponseEntity.ok(establishmentService.update(id, establishment));
    }

    // DELETE (soft delete)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        establishmentService.delete(id);
        return ResponseEntity.noContent().build();
    }
}