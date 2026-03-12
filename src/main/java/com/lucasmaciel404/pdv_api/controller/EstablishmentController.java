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

    @PostMapping
    public ResponseEntity<Establishment> create(@RequestBody Establishment establishment) {
        Establishment created = establishmentService.create(establishment);
        return ResponseEntity.ok(created);
    }
    @PostMapping("/{estabilishmentId}")
    public ResponseEntity<?> addUserToEstablishment(@PathVariable("estabilishmentId") UUID establishmentId, UUID userId ){
        return establishmentService.setUserToEstablishment(userId, establishmentId);
    }

    @GetMapping
    public ResponseEntity<List<Establishment>> findAll() {
        return ResponseEntity.ok(establishmentService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Establishment> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(establishmentService.findById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Establishment> update(
            @PathVariable UUID id,
            @RequestBody Establishment establishment
    ) {
        return ResponseEntity.ok(establishmentService.update(id, establishment));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        establishmentService.delete(id);
        return ResponseEntity.noContent().build();
    }
}