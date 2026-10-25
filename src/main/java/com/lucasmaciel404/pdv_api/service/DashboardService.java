
package com.lucasmaciel404.pdv_api.service;

import com.lucasmaciel404.pdv_api.dto.enums.model.OrderStatusEnum;
import com.lucasmaciel404.pdv_api.dto.enums.model.UserRoleEnum;
import com.lucasmaciel404.pdv_api.dto.response.DashboardResponse;
import com.lucasmaciel404.pdv_api.model.OrderModel;
import com.lucasmaciel404.pdv_api.model.OrderItemsModel;
import com.lucasmaciel404.pdv_api.model.UserEstablishment;
import com.lucasmaciel404.pdv_api.model.UserModel;
import com.lucasmaciel404.pdv_api.repository.OrderItemsRepository;
import com.lucasmaciel404.pdv_api.repository.OrderRepository;
import com.lucasmaciel404.pdv_api.repository.UserEstablishmentRepository;
import com.lucasmaciel404.pdv_api.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class DashboardService {

    private final OrderRepository orderRepository;
    private final OrderItemsRepository orderItemsRepository;
    private final UserRepository userRepository;
    private final UserEstablishmentRepository userEstablishmentRepository;

    public DashboardService(
            OrderRepository orderRepository,
            OrderItemsRepository orderItemsRepository,
            UserRepository userRepository,
            UserEstablishmentRepository userEstablishmentRepository
    ) {
        this.orderRepository = orderRepository;
        this.orderItemsRepository = orderItemsRepository;
        this.userRepository = userRepository;
        this.userEstablishmentRepository = userEstablishmentRepository;
    }

    @Transactional
    public DashboardResponse getDashboard(
            UUID establishmentId,
            String period,
            String email
    ) {
        validateOwner(email, establishmentId);

        LocalDate today = LocalDate.now();
        LocalDate startDate;

        switch (period) {
            case "today" -> startDate = today;
            case "7d" -> startDate = today.minusDays(6);
            case "30d" -> startDate = today.minusDays(29);
            default -> throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Período inválido. Utilize today, 7d ou 30d"
            );
        }

        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = today.plusDays(1).atStartOfDay();

        List<OrderModel> orders =
                orderRepository
                        .findByEstablishment_IdAndStatusAndCreatedAtGreaterThanEqualAndCreatedAtLessThan(
                                establishmentId,
                                OrderStatusEnum.CLOSED,
                                start,
                                end
                        );

        BigDecimal totalRevenue = BigDecimal.ZERO;
        Map<LocalDate, BigDecimal> revenueByDay = new HashMap<>();
        Map<UUID, ProductAccumulator> products = new HashMap<>();

        for (OrderModel order : orders) {
            List<OrderItemsModel> items =
                    orderItemsRepository.findAllByOrder_Id(order.getId());

            for (OrderItemsModel item : items) {
                BigDecimal itemRevenue = item.getUnitPrice()
                        .multiply(BigDecimal.valueOf(item.getQuantity()));

                LocalDate orderDate = order.getCreatedAt().toLocalDate();

                totalRevenue = totalRevenue.add(itemRevenue);

                revenueByDay.merge(
                        orderDate,
                        itemRevenue,
                        BigDecimal::add
                );

                UUID productId = item.getProduct().getId();

                ProductAccumulator accumulator = products.computeIfAbsent(
                        productId,
                        id -> new ProductAccumulator(
                                id,
                                item.getProduct().getName()
                        )
                );

                accumulator.quantity += item.getQuantity();
                accumulator.revenue =
                        accumulator.revenue.add(itemRevenue);
            }
        }

        List<DashboardResponse.SaleByDay> salesByDay =
                new ArrayList<>();

        for (LocalDate date = startDate;
             !date.isAfter(today);
             date = date.plusDays(1)) {

            salesByDay.add(
                    new DashboardResponse.SaleByDay(
                            date.toString(),
                            revenueByDay.getOrDefault(
                                    date,
                                    BigDecimal.ZERO
                            )
                    )
            );
        }

        List<DashboardResponse.TopProduct> topProducts =
                products.values()
                        .stream()
                        .sorted(
                                Comparator.comparingLong(
                                        (ProductAccumulator p) -> p.quantity
                                ).reversed()
                        )
                        .limit(5)
                        .map(p -> new DashboardResponse.TopProduct(
                                p.id.toString(),
                                p.name,
                                p.quantity,
                                p.revenue
                        ))
                        .toList();

        BigDecimal averageTicket = orders.isEmpty()
                ? BigDecimal.ZERO
                : totalRevenue.divide(
                BigDecimal.valueOf(orders.size()),
                2,
                RoundingMode.HALF_UP
        );

        return new DashboardResponse(
                totalRevenue,
                orders.size(),
                averageTicket,
                salesByDay,
                topProducts
        );
    }

    private void validateOwner(
            String email,
            UUID establishmentId
    ) {
        UserModel user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "Usuário autenticado não encontrado"
                ));

        if (!Boolean.TRUE.equals(user.getActive())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Usuário inativo"
            );
        }

        UserEstablishment membership =
                userEstablishmentRepository
                        .findByUser_IdAndEstablishment_Id(
                                user.getId(),
                                establishmentId
                        )
                        .orElseThrow(() -> new ResponseStatusException(
                                HttpStatus.FORBIDDEN,
                                "Você não possui acesso a este estabelecimento"
                        ));

        if (membership.getRole() != UserRoleEnum.OWNER) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Somente o proprietário pode visualizar o dashboard"
            );
        }

        if (!Boolean.TRUE.equals(
                membership.getEstablishment().getActive()
        )) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Estabelecimento inativo"
            );
        }
    }

    private static class ProductAccumulator {
        private final UUID id;
        private final String name;
        private long quantity;
        private BigDecimal revenue = BigDecimal.ZERO;

        private ProductAccumulator(UUID id, String name) {
            this.id = id;
            this.name = name;
        }
    }
}
