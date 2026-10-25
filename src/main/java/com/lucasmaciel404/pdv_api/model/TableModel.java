package com.lucasmaciel404.pdv_api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.lucasmaciel404.pdv_api.dto.enums.model.TableStatusEnum;
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
@Table(
        name = "tables",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_table_number_establishment",
                        columnNames = {"number", "establishment_id"}
                )
        }
)
public class TableModel {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private Integer number;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "card_id")
    private CardModel card;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TableStatusEnum status;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "establishment_id", nullable = false)
    private Establishment establishment;

    @Column(nullable = false)
    private Boolean active = true;

    public Boolean getActive() {
        return active;
    }
    @PrePersist
    private void prePersist() {
        if (status == null) status = TableStatusEnum.CLOSED;
        if (createdAt == null) createdAt = LocalDateTime.now();
    }
}