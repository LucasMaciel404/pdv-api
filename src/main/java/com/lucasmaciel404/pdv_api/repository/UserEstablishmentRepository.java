package com.lucasmaciel404.pdv_api.repository;

import com.lucasmaciel404.pdv_api.dto.enums.model.UserRoleEnum;
import com.lucasmaciel404.pdv_api.model.UserEstablishment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserEstablishmentRepository extends JpaRepository<UserEstablishment, UUID> {
    public Optional<UserEstablishment> findByUserId(UUID userId);

    boolean existsByUserIdAndEstablishmentIdAndRole(
            UUID userId,
            UUID establishmentId,
            UserRoleEnum role
    );

    boolean existsByUser_IdAndEstablishment_IdAndRole(
            UUID userId,
            UUID establishmentId,
            UserRoleEnum role
    );
    Optional<UserEstablishment>
    findByUser_IdAndEstablishment_Id(
            UUID userId,
            UUID establishmentId
    );
}
