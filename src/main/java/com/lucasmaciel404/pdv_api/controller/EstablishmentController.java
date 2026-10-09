package com.lucasmaciel404.pdv_api.controller;

import com.lucasmaciel404.pdv_api.dto.request.AddUserToEstablishmentRequest;
import com.lucasmaciel404.pdv_api.dto.response.AddUserToEstablishmentResponse;
import com.lucasmaciel404.pdv_api.dto.response.CreateEstablishmentResponse;
import com.lucasmaciel404.pdv_api.dto.response.FindAllEstablishmentsResponse;
import com.lucasmaciel404.pdv_api.dto.response.FindEstablishmentByIdResponse;
import com.lucasmaciel404.pdv_api.dto.response.FindEstablishmentsByUserResponse;
import com.lucasmaciel404.pdv_api.dto.response.UpdateEstablishmentResponse;
import com.lucasmaciel404.pdv_api.dto.mapper.EstablishmentMapper;
import com.lucasmaciel404.pdv_api.model.Establishment;
import com.lucasmaciel404.pdv_api.model.UserEstablishment;
import com.lucasmaciel404.pdv_api.service.EstablishmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/establishments")
public class EstablishmentController {

    private final EstablishmentService establishmentService;
    private final EstablishmentMapper establishmentMapper;

    @PostMapping
    public ResponseEntity<CreateEstablishmentResponse> create(
            @RequestBody Establishment establishment,
            Authentication authentication
    ) {
        String gmail = authentication.getName();

        Establishment created = establishmentService.create(establishment, gmail);

        if (created == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return ResponseEntity.ok(
                establishmentMapper.toCreateResponse(created)
        );
    }

    @PostMapping("/{establishmentId}")
    public ResponseEntity<AddUserToEstablishmentResponse> addUserToEstablishment(
            @PathVariable UUID establishmentId,
            @RequestBody AddUserToEstablishmentRequest request
    ) {
        UserEstablishment userEstablishment =
                establishmentService.setUserToEstablishment(
                        request.userId(),
                        establishmentId,
                        request.role()
                );

        AddUserToEstablishmentResponse response =
                establishmentMapper.toAddUserResponse(userEstablishment);

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<FindAllEstablishmentsResponse> findAll() {
        List<Establishment> establishments = establishmentService.findAll();

        return ResponseEntity.ok(
                establishmentMapper.toFindAllResponse(establishments)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<FindEstablishmentByIdResponse> findById(
            @PathVariable UUID id
    ) {
        Establishment establishment = establishmentService.findById(id);

        return ResponseEntity.ok(
                establishmentMapper.toFindByIdResponse(establishment)
        );
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<FindEstablishmentsByUserResponse> findByUser(
            @PathVariable UUID userId
    ) {
        Establishment establishment =
                establishmentService.getEstablishWithUserId(userId);

        return ResponseEntity.ok(
                establishmentMapper.toFindByUserResponse(establishment)
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<UpdateEstablishmentResponse> update(
            @PathVariable UUID id,
            @RequestBody Establishment establishment
    ) {
        Establishment updated = establishmentService.update(id, establishment);

        return ResponseEntity.ok(
                establishmentMapper.toUpdateResponse(updated)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        establishmentService.delete(id);

        return ResponseEntity.noContent().build();
    }
}
