package com.lucasmaciel404.pdv_api.service;

import com.lucasmaciel404.pdv_api.dto.PedidoDTO;
import com.lucasmaciel404.pdv_api.dto.enums.model.OrderStatusEnum;
import com.lucasmaciel404.pdv_api.dto.enums.model.TableStatusEnum;
import com.lucasmaciel404.pdv_api.dto.mapper.OrderItemMapper;
import com.lucasmaciel404.pdv_api.dto.request.AddItemsRequest;
import com.lucasmaciel404.pdv_api.dto.response.GetOrderByCardIdResponse;
import com.lucasmaciel404.pdv_api.dto.response.OrderItemResponse;
import com.lucasmaciel404.pdv_api.dto.response.ProductSummaryResponse;
import com.lucasmaciel404.pdv_api.model.*;
import com.lucasmaciel404.pdv_api.repository.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final TableRepository tableRepository;
    private final CardRepository cardRepository;
    private final OrderItemsRepository orderItemsRepository;
    private final ProductRepository productRepository;

    public GetOrderByCardIdResponse getOrderByCardId(UUID cardId) {
        CardModel card = cardRepository.findByUid(cardId).orElseThrow(()-> new EntityNotFoundException("Card not found"));
        if (card == null) throw new EntityNotFoundException("Card not found");

        // verificar se cardid esta vinculado a uma mesa
        TableModel table =  tableRepository.findByCard_Id(card.getId());
        if (table == null) throw new EntityNotFoundException("Card not linked to any table.");

        // pegar order vinculada a mesa
        OrderModel order = orderRepository.findByTable_IdAndStatus(table.getId(), OrderStatusEnum.OPEN);
        if (order == null) throw new EntityNotFoundException("Order not linked to any table.");

        List<OrderItemsModel> items = orderItemsRepository.findAllByOrder_Id(order.getId());

        List<OrderItemResponse> itemResponses = items.stream()
                .map(OrderItemMapper::toResponse)
                .toList();
        BigDecimal totalValue = itemResponses.stream()
                .map(OrderItemResponse::price) // ou item.price
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        // retornar
        return new GetOrderByCardIdResponse(
                order.getId().toString(),
                order.getStatus().name(),
                table.getNumber(),
                itemResponses,
                totalValue
        );
    }

    public OrderModel createOrderByCardId(UUID cardId,UUID tableId) {
        TableModel table = tableRepository.findById(tableId)
                .orElseThrow(() -> new EntityNotFoundException("Table not found"));
        CardModel card = cardRepository.findByUid(cardId)
                .orElseThrow(() -> new EntityNotFoundException("Card not found"));

        // vincular cardId a tabela
        table.setCard(card);
        table.setStatus(TableStatusEnum.OPEN);
        tableRepository.save(table);

        // criar comanda
        OrderModel order = new OrderModel();
        order.setStatus(OrderStatusEnum.OPEN);
        order.setTable(table);

        return orderRepository.save(order);
    }

    public OrderModel addItemsToOrder(UUID cardId, List<PedidoDTO> pedidos) {
        CardModel card =  cardRepository.findByUid(cardId)
                .orElseThrow(() -> new EntityNotFoundException("Card not found"));

        // verificar se cardid esta vinculado a uma mesa
        TableModel table =  tableRepository.findByCard_Id(card.getId());
        if (table == null) throw new EntityNotFoundException("Card not linked to any table.");

        // pegar order vinculada a mesa
        OrderModel order = orderRepository.findByTable_IdAndStatus(table.getId(), OrderStatusEnum.OPEN);
        if (order == null) throw new EntityNotFoundException("Order not linked to any table.");

        // pegando lista de produtos
        List<ProductModel> products = pedidos.stream().map((item)->{
            return productRepository.findById(item.productId()) .orElseThrow(() ->
                    new EntityNotFoundException(
                            String.format("Product not found with id %s", item.productId())
                    )
            );
        }).toList();

        // criando order Items
        List<OrderItemsModel> orderItems = products.stream().map((product) -> {

            PedidoDTO pedido = pedidos.stream()
                    .filter(p -> p.productId().equals(product.getId()))
                    .findFirst()
                    .orElseThrow();

            OrderItemsModel myOrderItem = new OrderItemsModel();
            myOrderItem.setProduct(product);
            myOrderItem.setOrder(order);
            myOrderItem.setQuantity(pedido.quantity());
            myOrderItem.setUnitPrice(product.getPrice().multiply(BigDecimal.valueOf(pedido.quantity())));

            return myOrderItem;
        }).toList();

        // salvar a lista de orderItems
        orderItemsRepository.saveAll(orderItems);
        return order;
    }

    public void deleteItemById(UUID itemId) {
        orderItemsRepository.deleteById(itemId);
    }
}
