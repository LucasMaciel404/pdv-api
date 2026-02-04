package com.lucasmaciel404.pdv_api.repository;

import com.lucasmaciel404.pdv_api.model.OrderModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OrderRepository extends JpaRepository<OrderModel, UUID> { }