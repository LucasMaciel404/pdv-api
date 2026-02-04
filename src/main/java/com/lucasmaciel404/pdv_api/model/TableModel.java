package com.lucasmaciel404.pdv_api.model;
import com.lucasmaciel404.pdv_api.enums.model.TableStatusEnum;
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
@Table(name="tables")
public class TableModel {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false, unique = true)
    private Integer number;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "card_id")
    private CardModel card;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TableStatusEnum status;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    private void prePersist() {
        if (status == null) status = TableStatusEnum.CLOSED;
        if (createdAt == null) createdAt = LocalDateTime.now();
    }
}