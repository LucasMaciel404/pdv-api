
package com.lucasmaciel404.pdv_api.model;

import com.lucasmaciel404.pdv_api.dto.enums.model.OrderStatusEnum;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "orders")
public class OrderModel {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "table_id", nullable = false)
    private TableModel table;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatusEnum status;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "closed_at")
    private LocalDateTime closedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "establishment_id", nullable = false)
    private Establishment establishment;

    @Column(name = "subtotal_amount", precision = 12, scale = 2)
    private BigDecimal subtotalAmount;

    @Column(
            name = "service_fee_percentage",
            precision = 5,
            scale = 2
    )
    private BigDecimal serviceFeePercentage;

    @Column(name = "service_fee_amount", precision = 12, scale = 2)
    private BigDecimal serviceFeeAmount;

    @Column(name = "total_amount", precision = 12, scale = 2)
    private BigDecimal totalAmount;

    @PrePersist
    private void prePersist() {
        if (status == null) {
            status = OrderStatusEnum.OPEN;
        }

        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }

        if (subtotalAmount == null) {
            subtotalAmount = BigDecimal.ZERO;
        }

        if (serviceFeePercentage == null) {
            serviceFeePercentage = BigDecimal.ZERO;
        }

        if (serviceFeeAmount == null) {
            serviceFeeAmount = BigDecimal.ZERO;
        }

        if (totalAmount == null) {
            totalAmount = BigDecimal.ZERO;
        }
    }
}
