package com.lucasmaciel404.pdv_api.model;

import jakarta.persistence.*;


import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "establishments")
public class Establishment {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false, length = 150)
    private String name;

    private Boolean active;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "establishment")
    private List<ProductModel> products;

    @OneToMany(mappedBy = "establishment")
    private List<TableModel> tables;

    @OneToMany(mappedBy = "establishment")
    private List<CardModel> cards;

    @OneToMany(mappedBy = "establishment")
    private List<OrderModel> orders;

    @OneToMany(mappedBy = "establishment")
    private List<UserEstablishment> users;
}