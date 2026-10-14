package com.lucasmaciel404.pdv_api.service;

import com.lucasmaciel404.pdv_api.dto.enums.model.JoinRequestStatus;
import com.lucasmaciel404.pdv_api.model.EstablishmentJoinRequest;
import com.lucasmaciel404.pdv_api.repository.EstablishmentJoinRequestRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class EstablishmentJoinRequestService {

    private final EstablishmentJoinRequestRepository repository;

    public EstablishmentJoinRequestService(
            EstablishmentJoinRequestRepository repository
    ) {
        this.repository = repository;
    }

    @Transactional
    public EstablishmentJoinRequest create(
            UUID establishmentId,
            UUID userId
    ) {
        boolean alreadyRequested =
                repository.existsByEstablishmentIdAndUserIdAndStatus(
                        establishmentId,
                        userId,
                        JoinRequestStatus.PENDING
                );

        if (alreadyRequested) {
            throw new IllegalArgumentException(
                    "Já existe uma solicitação pendente para este estabelecimento."
            );
        }

        EstablishmentJoinRequest request =
                new EstablishmentJoinRequest();

        request.setEstablishmentId(establishmentId);
        request.setUserId(userId);
        request.setStatus(JoinRequestStatus.PENDING);

        return repository.save(request);
    }

    @Transactional(readOnly = true)
    public List<EstablishmentJoinRequest> findByEstablishment(
            UUID establishmentId
    ) {
        return repository.findByEstablishmentId(establishmentId);
    }

    @Transactional(readOnly = true)
    public List<EstablishmentJoinRequest> findPendingByEstablishment(
            UUID establishmentId
    ) {
        return repository.findByEstablishmentIdAndStatus(
                establishmentId,
                JoinRequestStatus.PENDING
        );
    }

    @Transactional
    public EstablishmentJoinRequest updateStatus(
            UUID requestId,
            JoinRequestStatus status
    ) {
        EstablishmentJoinRequest request = repository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Solicitação não encontrada."
                ));

        request.setStatus(status);

        return repository.save(request);
    }
    @Transactional(readOnly = true)
    public boolean hasAcceptedRequest(
            UUID establishmentId,
            UUID userId
    ) {
        return repository.existsByEstablishmentIdAndUserIdAndStatus(
                establishmentId,
                userId,
                JoinRequestStatus.ACCEPTED
        );
    }
}