package com.lucasmaciel404.pdv_api.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "cards")
public class CardModel {
    @Id
    @GeneratedValue
    private UUID id;

    @Column(unique = true, nullable = false)
    private UUID uid;

    private Boolean active;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    void onCreate() {
        if (active == null) active = true;
        if (createdAt == null) createdAt = LocalDateTime.now();
        if (uid == null) uid = UUID.randomUUID();
    }
}
