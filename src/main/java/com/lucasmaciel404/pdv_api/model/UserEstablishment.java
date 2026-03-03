package com.lucasmaciel404.pdv_api.model;

import com.lucasmaciel404.pdv_api.dto.enums.model.UserRoleEnum;
import jakarta.persistence.*;
import lombok.RequiredArgsConstructor;

import java.util.UUID;
@Entity
@Table(
        name = "user_establishments",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"user_id", "establishment_id"})
        }
)
public class UserEstablishment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserModel user;

    @ManyToOne
    @JoinColumn(name = "establishment_id", nullable = false)
    private Establishment establishment;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRoleEnum role;
}