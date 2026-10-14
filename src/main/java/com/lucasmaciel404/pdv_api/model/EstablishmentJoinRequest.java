package com.lucasmaciel404.pdv_api.model;

import com.lucasmaciel404.pdv_api.dto.enums.model.JoinRequestStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Entity
@Table(name = "establishment_join_requests")
@Getter
@Setter
public class EstablishmentJoinRequest {

    @Id
    @UuidGenerator
    private UUID id;

    @Column(name = "establishment_id", nullable = false)
    private UUID establishmentId;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private JoinRequestStatus status = JoinRequestStatus.PENDING;
}