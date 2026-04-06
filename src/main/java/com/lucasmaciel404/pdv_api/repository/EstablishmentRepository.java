package com.lucasmaciel404.pdv_api.repository;

import com.lucasmaciel404.pdv_api.model.Establishment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface EstablishmentRepository extends JpaRepository<Establishment, UUID> {
}
