package com.lucasmaciel404.pdv_api.controller;

import com.lucasmaciel404.pdv_api.dto.enums.model.TableStatusEnum;
import com.lucasmaciel404.pdv_api.model.TableModel;
import com.lucasmaciel404.pdv_api.service.TableService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/tables")
@RequiredArgsConstructor
public class TableController {

    private final TableService tableService;

    // 🔎 Buscar por ID
    @GetMapping("/{id}")
    public ResponseEntity<TableModel> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(tableService.findById(id));
    }

    // 🔎 Buscar por CardId
    @GetMapping("/card/{cardId}")
    public ResponseEntity<TableModel> findByCardId(@PathVariable UUID cardId) {
        return ResponseEntity.ok(tableService.findByCardId(cardId));
    }

    // 📋 Listar todas
    @GetMapping
    public ResponseEntity<List<TableModel>> findAll() {
        return ResponseEntity.ok(tableService.findAll());
    }

    // ➕ Criar mesa
    @PostMapping
    public ResponseEntity<TableModel> create(@RequestBody CreateTableRequest request) {
        TableModel table = tableService.create(
                request.number(),
                request.establishmentId()
        );
        return ResponseEntity.ok(table);
    }

    // 🔄 Atualizar número
    @PutMapping("/{id}")
    public ResponseEntity<TableModel> update(
            @PathVariable UUID id,
            @RequestBody UpdateTableRequest request
    ) {
        return ResponseEntity.ok(tableService.update(id, request.number()));
    }

    // 🔗 Vincular cartão
    @PatchMapping("/{id}/card")
    public ResponseEntity<TableModel> attachCard(
            @PathVariable UUID id,
            @RequestBody AttachCardRequest request
    ) {
        return ResponseEntity.ok(
                tableService.attachCard(id, request.cardId())
        );
    }

    // ❌ Deletar
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        tableService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // 🔹 Requests DTOs internos simples

    public record CreateTableRequest(
            Integer number,
            UUID establishmentId
    ) {
    }

    public record UpdateTableRequest(
            Integer number
    ) {
    }

    public record AttachCardRequest(
            UUID cardId
    ) {
    }
}