package com.lucasmaciel404.pdv_api.controller;

import com.lucasmaciel404.pdv_api.dto.mapper.TableMapper;
import com.lucasmaciel404.pdv_api.dto.request.AttachCardRequest;
import com.lucasmaciel404.pdv_api.dto.request.CreateTableRequest;
import com.lucasmaciel404.pdv_api.dto.request.UpdateTableRequest;
import com.lucasmaciel404.pdv_api.dto.response.TableResponse;
import com.lucasmaciel404.pdv_api.model.TableModel;
import com.lucasmaciel404.pdv_api.service.TableService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/tables")
@RequiredArgsConstructor
public class TableController {

    private final TableService tableService;
    private final TableMapper tableMapper;

    @GetMapping("/{id}")
    public ResponseEntity<TableResponse> findById(
            @PathVariable UUID id,
            Authentication authentication
    ) {
        TableModel table = tableService.findById(
                id,
                authentication.getName()
        );

        return ResponseEntity.ok(tableMapper.toResponse(table));
    }

    @GetMapping("/card/{cardId}")
    public ResponseEntity<TableResponse> findByCardId(
            @PathVariable UUID cardId,
            Authentication authentication
    ) {
        TableModel table = tableService.findByCardId(
                cardId,
                authentication.getName()
        );

        return ResponseEntity.ok(tableMapper.toResponse(table));
    }

    @GetMapping("/establishment/{establishmentId}")
    public ResponseEntity<List<TableResponse>> findByEstablishmentId(
            @PathVariable UUID establishmentId,
            Authentication authentication
    ) {
        List<TableResponse> response = tableService
                .findByEstablishmentId(
                        establishmentId,
                        authentication.getName()
                )
                .stream()
                .map(tableMapper::toResponse)
                .toList();

        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<TableResponse> create(
            @Valid @RequestBody CreateTableRequest request,
            Authentication authentication
    ) {
        TableModel table = tableService.create(
                request.number(),
                request.establishment(),
                authentication.getName()
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(tableMapper.toResponse(table));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TableResponse> update(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateTableRequest request,
            Authentication authentication
    ) {
        TableModel table = tableService.update(
                id,
                request.number(),
                authentication.getName()
        );

        return ResponseEntity.ok(tableMapper.toResponse(table));
    }

    @PatchMapping("/{id}/card")
    public ResponseEntity<TableResponse> attachCard(
            @PathVariable UUID id,
            @Valid @RequestBody AttachCardRequest request,
            Authentication authentication
    ) {
        TableModel table = tableService.attachCard(
                id,
                request.cardId(),
                authentication.getName()
        );

        return ResponseEntity.ok(tableMapper.toResponse(table));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable UUID id,
            Authentication authentication
    ) {
        tableService.delete(id, authentication.getName());

        return ResponseEntity.noContent().build();
    }
}