package com.lucasmaciel404.pdv_api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
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
    @JsonIgnore
    private List<ProductModel> products;

    @OneToMany(mappedBy = "establishment")
    @JsonIgnore
    private List<TableModel> tables;

    @OneToMany(mappedBy = "establishment")
    @JsonIgnore
    private List<CardModel> cards;

    @OneToMany(mappedBy = "establishment")
    @JsonIgnore
    private List<OrderModel> orders;

    @OneToMany(mappedBy = "establishment")
    @JsonIgnore
    private List<UserEstablishment> users;

    @PrePersist
    void onCreate() {
        if (active == null) active = true;
        if (createdAt == null) createdAt = LocalDateTime.now();
    }
}