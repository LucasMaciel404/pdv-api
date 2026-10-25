package com.lucasmaciel404.pdv_api.repository;

import com.lucasmaciel404.pdv_api.model.CardModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CardRepository extends JpaRepository<CardModel, UUID> {
    Optional<CardModel> findByCardCode(String cardCode);
    Optional<CardModel> findByIdAndEstablishmentId(
            UUID id,
            UUID establishmentId
    );
    List<CardModel> findByEstablishmentId(UUID establishmentId);
    Optional<CardModel> findByCardCodeAndEstablishmentId(
            String cardCode,
            UUID establishmentId
    );
}
