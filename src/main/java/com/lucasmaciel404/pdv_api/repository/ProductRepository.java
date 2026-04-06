package com.lucasmaciel404.pdv_api.repository;

import com.lucasmaciel404.pdv_api.model.ProductModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<ProductModel, UUID> {
    List<ProductModel> findByEstablishmentId(UUID id);
}
