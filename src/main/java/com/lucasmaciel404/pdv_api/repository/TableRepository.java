package com.lucasmaciel404.pdv_api.repository;

import com.lucasmaciel404.pdv_api.model.TableModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TableRepository extends JpaRepository<TableModel, UUID> { }
