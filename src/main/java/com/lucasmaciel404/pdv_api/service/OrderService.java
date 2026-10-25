package com.lucasmaciel404.pdv_api.service;

import com.lucasmaciel404.pdv_api.dto.PedidoDTO;
import com.lucasmaciel404.pdv_api.dto.enums.model.OrderStatusEnum;
import com.lucasmaciel404.pdv_api.dto.enums.model.TableStatusEnum;
import com.lucasmaciel404.pdv_api.dto.mapper.OrderItemMapper;
import com.lucasmaciel404.pdv_api.dto.mapper.OrderMapper;
import com.lucasmaciel404.pdv_api.dto.response.CreateOrderResponse;
import com.lucasmaciel404.pdv_api.dto.response.GetOrderByCardIdResponse;
import com.lucasmaciel404.pdv_api.dto.response.OrderItemResponse;
import com.lucasmaciel404.pdv_api.model.CardModel;
import com.lucasmaciel404.pdv_api.model.OrderItemsModel;
import com.lucasmaciel404.pdv_api.model.OrderModel;
import com.lucasmaciel404.pdv_api.model.ProductModel;
import com.lucasmaciel404.pdv_api.model.TableModel;
import com.lucasmaciel404.pdv_api.repository.CardRepository;
import com.lucasmaciel404.pdv_api.repository.OrderItemsRepository;
import com.lucasmaciel404.pdv_api.repository.OrderRepository;
import com.lucasmaciel404.pdv_api.repository.ProductRepository;
import com.lucasmaciel404.pdv_api.repository.TableRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import java.math.BigDecimal;
import java.util.List;
import java.math.RoundingMode;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final TableRepository tableRepository;
    private final CardRepository cardRepository;
    private final OrderItemsRepository orderItemsRepository;
    private final ProductRepository productRepository;

    public GetOrderByCardIdResponse getOrderByCardId(String cardId) {
        CardModel card = cardRepository.findByCardCode(cardId)
                .orElseThrow(() ->
                        new EntityNotFoundException("Card not found")
                );

        TableModel table = tableRepository.findByCard_Id(card.getId())
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "Card not linked to any table."
                        )
                );

        OrderModel order = orderRepository.findByTable_IdAndStatus(
                table.getId(),
                OrderStatusEnum.OPEN
        );

        if (order == null) {
            throw new EntityNotFoundException(
                    "Order not linked to any table."
            );
        }

        List<OrderItemsModel> items =
                orderItemsRepository.findAllByOrder_Id(order.getId());

        List<OrderItemResponse> itemResponses = items.stream()
                .map(OrderItemMapper::toResponse)
                .toList();

        BigDecimal subtotalAmount = items.stream()
                .map(item -> item.getUnitPrice()
                        .multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal serviceFeePercentage = order.getEstablishment()
                .getDefaultServiceFeePercentage();

        BigDecimal serviceFeeAmount = subtotalAmount
                .multiply(serviceFeePercentage)
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

        BigDecimal totalAmount = subtotalAmount.add(serviceFeeAmount);

        return new GetOrderByCardIdResponse(
                order.getId().toString(),
                order.getStatus().name(),
                table.getNumber(),
                itemResponses,
                subtotalAmount,
                serviceFeePercentage,
                serviceFeeAmount,
                totalAmount,
                order.getCreatedAt(),
                order.getClosedAt()
        );
    }

    public CreateOrderResponse createOrderByCardId(
            String cardId,
            UUID tableId
    ) {
        TableModel table = tableRepository.findById(tableId)
                .orElseThrow(() ->
                        new EntityNotFoundException("Table not found"));

        CardModel card = cardRepository.findByCardCode(cardId)
                .orElseThrow(() ->
                        new EntityNotFoundException("Card not found"));

        table.setCard(card);
        table.setStatus(TableStatusEnum.OPEN);
        tableRepository.save(table);

        OrderModel order = new OrderModel();
        order.setStatus(OrderStatusEnum.OPEN);
        order.setTable(table);
        order.setEstablishment(table.getEstablishment());

        OrderModel savedOrder = orderRepository.save(order);

        return OrderMapper.toCreateResponse(savedOrder);
    }


    @Transactional
    public OrderModel addItemsToOrder(
            String cardCode,
            UUID tableId,
            List<PedidoDTO> pedidos
    ) {
        if (cardCode == null || cardCode.isBlank()) {
            throw new IllegalArgumentException("Card code is required.");
        }

        if (tableId == null) {
            throw new IllegalArgumentException("Table ID is required.");
        }

        if (pedidos == null || pedidos.isEmpty()) {
            throw new IllegalArgumentException("Order must contain at least one item.");
        }

        CardModel card = cardRepository.findByCardCode(cardCode)
                .orElseThrow(() ->
                        new EntityNotFoundException("Card not found."));

        TableModel table = tableRepository.findById(tableId)
                .orElseThrow(() ->
                        new EntityNotFoundException("Table not found."));

        // Verifica se o cartão já está vinculado a outra mesa
        Optional<TableModel> linkedTable =
                tableRepository.findByCard_Id(card.getId());

        if (linkedTable.isPresent()
                && !linkedTable.get().getId().equals(tableId)) {
            throw new IllegalStateException(
                    "Card is already linked to another table."
            );
        }

        // Impede substituir um cartão que já pertence à mesa
        if (table.getCard() != null
                && !table.getCard().getId().equals(card.getId())) {
            throw new IllegalStateException(
                    "Table is already linked to another card."
            );
        }

        // Vincula o cartão caso a mesa ainda não tenha um
        if (table.getCard() == null) {
            table.setCard(card);
            table.setStatus(TableStatusEnum.CLOSED);
            tableRepository.save(table);
        }

        // Busca o pedido aberto ou cria um novo
        OrderModel order = orderRepository.findByTable_IdAndStatus(
                table.getId(),
                OrderStatusEnum.OPEN
        );

        if (order == null) {
            order = new OrderModel();
            order.setStatus(OrderStatusEnum.OPEN);
            order.setTable(table);
            order.setEstablishment(table.getEstablishment());

            order = orderRepository.save(order);
        }

        final OrderModel currentOrder = order;

        List<OrderItemsModel> orderItems = pedidos.stream()
                .map(pedido -> {
                    if (pedido == null) {
                        throw new IllegalArgumentException(
                                "Order item cannot be null."
                        );
                    }

                    if (pedido.productId() == null) {
                        throw new IllegalArgumentException(
                                "Product ID is required."
                        );
                    }

                    if (pedido.quantity() == null || pedido.quantity() <= 0) {
                        throw new IllegalArgumentException(
                                "Product quantity must be greater than zero."
                        );
                    }

                    ProductModel product = productRepository
                            .findById(pedido.productId())
                            .orElseThrow(() ->
                                    new EntityNotFoundException(
                                            "Product not found with id "
                                                    + pedido.productId()
                                    )
                            );

                    Optional<OrderItemsModel> existingItem =
                            orderItemsRepository.findByOrderIdAndProductId(
                                    currentOrder.getId(),
                                    product.getId()
                            );

                    if (existingItem.isPresent()) {
                        OrderItemsModel item = existingItem.get();

                        item.setQuantity(
                                item.getQuantity() + pedido.quantity()
                        );

                        return item;
                    }

                    OrderItemsModel orderItem = new OrderItemsModel();
                    orderItem.setProduct(product);
                    orderItem.setOrder(currentOrder);
                    orderItem.setQuantity(pedido.quantity());
                    orderItem.setUnitPrice(product.getPrice());

                    return orderItem;
                })
                .toList();

        orderItemsRepository.saveAll(orderItems);

        recalculateSubtotal(order);

        return order;
    }


    @Transactional
    public void closeByOrderId(UUID orderId) {
        OrderModel order = orderRepository.findById(orderId)
                .orElseThrow(() ->
                        new EntityNotFoundException("Order not found: " + orderId));

        if (order.getStatus() != OrderStatusEnum.OPEN) {
            throw new IllegalStateException("Order is not open: " + orderId);
        }

        TableModel table = tableRepository.findById(order.getTable().getId())
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "Table not found: " + order.getTable().getId()));

        // Calcula o subtotal usando os preços registrados nos itens.
        BigDecimal subtotal = orderItemsRepository
                .findAllByOrder_Id(order.getId())
                .stream()
                .map(item -> item.getUnitPrice()
                        .multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);

        // Obtém a taxa configurada para o estabelecimento.
        BigDecimal percentage = order.getEstablishment()
                .getDefaultServiceFeePercentage();

        if (percentage == null) {
            percentage = BigDecimal.ZERO;
        }

        if (percentage.compareTo(BigDecimal.ZERO) < 0
                || percentage.compareTo(new BigDecimal("100")) > 0) {
            throw new IllegalStateException(
                    "Service fee percentage must be between 0 and 100.");
        }

        // Calcula a taxa e o total final.
        BigDecimal serviceFee = subtotal
                .multiply(percentage)
                .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);

        BigDecimal total = subtotal.add(serviceFee)
                .setScale(2, RoundingMode.HALF_UP);

        // Salva os valores financeiros do pedido.
        order.setSubtotalAmount(subtotal);
        order.setServiceFeePercentage(percentage);
        order.setServiceFeeAmount(serviceFee);
        order.setTotalAmount(total);
        order.setClosedAt(LocalDateTime.now());
        order.setStatus(OrderStatusEnum.CLOSED);

        orderRepository.save(order);

        // Libera o cartão NFC e a mesa.
        table.setCard(null);
        table.setStatus(TableStatusEnum.OPEN);
        tableRepository.save(table);
    }


    @Transactional
    public void deleteItemById(UUID itemId) {
        OrderItemsModel item = orderItemsRepository.findById(itemId)
                .orElseThrow(() ->
                        new EntityNotFoundException("Order item not found"));

        OrderModel order = item.getOrder();

        if (order.getStatus() != OrderStatusEnum.OPEN) {
            throw new IllegalStateException(
                    "Cannot delete items from a closed order.");
        }

        orderItemsRepository.delete(item);

        recalculateSubtotal(order);
    }


    private void recalculateSubtotal(OrderModel order) {
        BigDecimal subtotal = orderItemsRepository
                .findAllByOrder_Id(order.getId())
                .stream()
                .map(item -> item.getUnitPrice()
                        .multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);

        order.setSubtotalAmount(subtotal);
        orderRepository.save(order);
    }
}