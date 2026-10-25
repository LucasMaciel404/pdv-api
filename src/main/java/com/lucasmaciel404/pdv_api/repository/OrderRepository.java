package com.lucasmaciel404.pdv_api.repository;

import com.lucasmaciel404.pdv_api.dto.enums.model.OrderStatusEnum;
import com.lucasmaciel404.pdv_api.dto.enums.model.TableStatusEnum;
import com.lucasmaciel404.pdv_api.model.OrderModel;
import jakarta.persistence.criteria.AbstractQuery;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<OrderModel, UUID> {
    OrderModel findByTable_IdAndStatus(UUID id, OrderStatusEnum status);
    boolean existsByTable_Id(UUID tableId);

    boolean existsByTable_IdAndStatus(
            UUID tableId,
            OrderStatusEnum status
    );

    List<OrderModel>
    findByEstablishment_IdAndStatusAndCreatedAtGreaterThanEqualAndCreatedAtLessThan(
            UUID establishmentId,
            OrderStatusEnum status,
            LocalDateTime start,
            LocalDateTime end
    );

}