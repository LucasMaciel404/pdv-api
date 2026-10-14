package com.lucasmaciel404.pdv_api.repository;

import com.lucasmaciel404.pdv_api.model.TableModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TableRepository extends JpaRepository<TableModel, UUID> {
    TableModel findByCard_Id(UUID uuid);

    Optional<TableModel> findByCardId(UUID cardId);

    List<TableModel> findByEstablishmentId(UUID establishmentId);

    Optional<TableModel> findFirstByCardIsNull();
}