package com.lucasmaciel404.pdv_api.repository;

import com.lucasmaciel404.pdv_api.model.OrderItemsModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface OrderItemsRepository extends JpaRepository<OrderItemsModel, UUID> {

    List<OrderItemsModel> findAllByOrder_Id(UUID id);
}
