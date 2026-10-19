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

import java.util.Optional;
import java.util.UUID;

import java.math.BigDecimal;
import java.util.List;
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
                .orElseThrow(() -> new EntityNotFoundException("Card not found"));

        TableModel table = tableRepository.findByCard_Id(card.getId());

        if (table == null) {
            throw new EntityNotFoundException("Card not linked to any table.");
        }

        OrderModel order = orderRepository.findByTable_IdAndStatus(
                table.getId(),
                OrderStatusEnum.OPEN
        );

        if (order == null) {
            throw new EntityNotFoundException("Order not linked to any table.");
        }

        List<OrderItemsModel> items =
                orderItemsRepository.findAllByOrder_Id(order.getId());

        List<OrderItemResponse> itemResponses = items.stream()
                .map(OrderItemMapper::toResponse)
                .toList();

        BigDecimal totalValue = itemResponses.stream()
                .map(OrderItemResponse::price)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new GetOrderByCardIdResponse(
                order.getId().toString(),
                order.getStatus().name(),
                table.getNumber(),
                itemResponses,
                totalValue
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
        TableModel linkedTable = tableRepository.findByCard_Id(card.getId());

        if (linkedTable != null && !linkedTable.getId().equals(tableId)) {
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

        return order;
    }

    @Transactional
    public void closeByOrderId(UUID orderId) {
        System.out.println(" AAAAAAAAAAAAAAAAAAAAAA:>>>>> "+orderId);
        OrderModel order = orderRepository.findById(orderId)
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "Order not found: " + orderId
                        )
                );


        if (order.getStatus() != OrderStatusEnum.OPEN) {
            throw new IllegalStateException(
                    "Order is not open: " + orderId
            );
        }

        TableModel table = tableRepository.findById(order.getTable().getId())
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "Table not found: " + order.getTable().getId()
                        )
                );

        // Remove o cartão NFC associado à mesa
        table.setCard(null);

        // Libera a mesa
        table.setStatus(TableStatusEnum.OPEN);

        tableRepository.save(table);

        // Fecha o pedido
        order.setStatus(OrderStatusEnum.CLOSED);
        orderRepository.save(order);
    }

    public void deleteItemById(UUID itemId) {
        if (!orderItemsRepository.existsById(itemId)) {
            throw new EntityNotFoundException("Order item not found");
        }

        orderItemsRepository.deleteById(itemId);
    }
}