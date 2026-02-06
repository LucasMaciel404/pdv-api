package com.lucasmaciel404.pdv_api.repository;

import com.lucasmaciel404.pdv_api.model.CardModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CardRepository extends JpaRepository<CardModel, UUID> {
    CardModel findByUid(UUID cardId);
}
