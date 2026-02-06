package com.lucasmaciel404.pdv_api.service;

import com.lucasmaciel404.pdv_api.dto.response.GetOrderByCardIdResponse;
import com.lucasmaciel404.pdv_api.dto.response.OrderItemResponse;
import com.lucasmaciel404.pdv_api.dto.response.ProductSummaryResponse;
import com.lucasmaciel404.pdv_api.model.CardModel;
import com.lucasmaciel404.pdv_api.model.OrderItemsModel;
import com.lucasmaciel404.pdv_api.model.OrderModel;
import com.lucasmaciel404.pdv_api.model.TableModel;
import com.lucasmaciel404.pdv_api.repository.CardRepository;
import com.lucasmaciel404.pdv_api.repository.OrderItemsRepository;
import com.lucasmaciel404.pdv_api.repository.OrderRepository;
import com.lucasmaciel404.pdv_api.repository.TableRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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

    public GetOrderByCardIdResponse getOrderByCardId(UUID cardId) {
        CardModel card = cardRepository.findByUid(cardId);
        if (card == null) throw new EntityNotFoundException("Card not found");

        // verificar se cardid esta vinculado a uma mesa
        TableModel table =  tableRepository.findByCard_Id(card.getId());
        if (table == null) throw new EntityNotFoundException("Card not linked to any table.");

        // pegar order vinculada a mesa
        OrderModel order = orderRepository.findByTable_Id(table.getId());
        if (order == null) throw new EntityNotFoundException("Order not linked to any table.");

        List<OrderItemsModel> items = orderItemsRepository.findAllByOrder_Id(order.getId());

        List<OrderItemResponse> itemResponses = items.stream()
                .map(item -> new OrderItemResponse(
                        item.getId().toString(),
                        item.getQuantity(),
                        item.getUnitPrice(),
                        new ProductSummaryResponse(
                                item.getProduct().getId().toString(),
                                item.getProduct().getName(),
                                item.getProduct().getPrice()
                        )
                ))
                .toList();

        // retornar
        return new GetOrderByCardIdResponse(
                order.getId().toString(),
                order.getStatus().name(),
                table.getNumber(),
                itemResponses
        );
    }
}
