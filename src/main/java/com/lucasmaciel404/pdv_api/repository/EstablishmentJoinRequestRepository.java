package com.lucasmaciel404.pdv_api.repository;

import com.lucasmaciel404.pdv_api.dto.enums.model.JoinRequestStatus;
import com.lucasmaciel404.pdv_api.model.EstablishmentJoinRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface EstablishmentJoinRequestRepository
        extends JpaRepository<EstablishmentJoinRequest, UUID> {

    boolean existsByEstablishmentIdAndUserIdAndStatus(
            UUID establishmentId,
            UUID userId,
            JoinRequestStatus status
    );

    List<EstablishmentJoinRequest> findByEstablishmentId(
            UUID establishmentId
    );

    List<EstablishmentJoinRequest> findByEstablishmentIdAndStatus(
            UUID establishmentId,
            JoinRequestStatus status
    );
}