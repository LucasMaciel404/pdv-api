package com.lucasmaciel404.pdv_api.repository;

import com.lucasmaciel404.pdv_api.model.UserEstablishment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserEstablishmentRepository  extends JpaRepository<UserEstablishment, UUID> {
    public Optional<UserEstablishment> findByUserId(UUID userId);
}
