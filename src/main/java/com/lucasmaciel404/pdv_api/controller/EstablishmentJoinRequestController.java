package com.lucasmaciel404.pdv_api.controller;

import com.lucasmaciel404.pdv_api.dto.CreateJoinRequestRequest;
import com.lucasmaciel404.pdv_api.dto.UpdateJoinRequestStatusRequest;
import com.lucasmaciel404.pdv_api.dto.enums.model.JoinRequestStatus;
import com.lucasmaciel404.pdv_api.model.EstablishmentJoinRequest;
import com.lucasmaciel404.pdv_api.service.EstablishmentJoinRequestService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/establishment-join-requests")
public class EstablishmentJoinRequestController {

    private final EstablishmentJoinRequestService service;

    public EstablishmentJoinRequestController(
            EstablishmentJoinRequestService service
    ) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<EstablishmentJoinRequest> create(
            @RequestBody CreateJoinRequestRequest request
    ) {
        EstablishmentJoinRequest created = service.create(
                request.establishmentId(),
                request.userId()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/establishment/{establishmentId}")
    public ResponseEntity<List<EstablishmentJoinRequest>> findByEstablishment(
            @PathVariable UUID establishmentId
    ) {
        return ResponseEntity.ok(
                service.findByEstablishment(establishmentId)
        );
    }

    @GetMapping("/establishment/{establishmentId}/pending")
    public ResponseEntity<List<EstablishmentJoinRequest>> findPending(
            @PathVariable UUID establishmentId
    ) {
        return ResponseEntity.ok(
                service.findPendingByEstablishment(establishmentId)
        );
    }

    @PatchMapping("/{requestId}/status")
    public ResponseEntity<EstablishmentJoinRequest> updateStatus(
            @PathVariable UUID requestId,
            @RequestBody UpdateJoinRequestStatusRequest request
    ) {
        if (request.status() == null
                || request.status() == JoinRequestStatus.PENDING) {
            throw new IllegalArgumentException(
                    "O status deve ser ACCEPTED ou REJECTED."
            );
        }

        return ResponseEntity.ok(
                service.updateStatus(requestId, request.status())
        );
    }
}