package com.lucasmaciel404.pdv_api.repository;

import com.lucasmaciel404.pdv_api.model.TableModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TableRepository
        extends JpaRepository<TableModel, UUID> {

    Optional<TableModel> findByCard_Id(UUID cardId);

    List<TableModel> findByEstablishmentId(UUID establishmentId);

    Optional<TableModel> findByIdAndEstablishmentId(
            UUID id,
            UUID establishmentId
    );

    boolean existsByNumberAndEstablishmentId(
            Integer number,
            UUID establishmentId
    );

    boolean existsByNumberAndEstablishmentIdAndIdNot(
            Integer number,
            UUID establishmentId,
            UUID id
    );

    boolean existsByCard_Id(UUID cardId);

    boolean existsByCard_IdAndIdNot(UUID cardId, UUID tableId);

    Optional<TableModel> findByNumberAndEstablishmentId(
            Integer number,
            UUID establishmentId
    );
}