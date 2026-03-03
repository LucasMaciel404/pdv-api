package com.lucasmaciel404.pdv_api.service;

import com.lucasmaciel404.pdv_api.dto.enums.model.TableStatusEnum;
import com.lucasmaciel404.pdv_api.model.CardModel;
import com.lucasmaciel404.pdv_api.model.Establishment;
import com.lucasmaciel404.pdv_api.model.TableModel;
import com.lucasmaciel404.pdv_api.repository.CardRepository;
import com.lucasmaciel404.pdv_api.repository.EstablishmentRepository;
import com.lucasmaciel404.pdv_api.repository.TableRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class TableService {

    private final TableRepository tableRepository;
    private final CardRepository cardRepository;
    private final EstablishmentRepository establishmentRepository;

    public TableService(TableRepository tableRepository,
                        CardRepository cardRepository,
                        EstablishmentRepository establishmentRepository) {
        this.tableRepository = tableRepository;
        this.cardRepository = cardRepository;
        this.establishmentRepository = establishmentRepository;
    }

    public TableModel findById(UUID id) {
        return tableRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Mesa não encontrada"));
    }

    public TableModel findByCardId(UUID cardId) {
        return tableRepository.findByCardId(cardId)
                .orElseThrow(() -> new EntityNotFoundException("Mesa não encontrada para esse cartão"));
    }

    public List<TableModel> findAll() {
        return tableRepository.findAll();
    }

    public TableModel create(Integer number, UUID establishmentId) {

        Establishment establishment = establishmentRepository.findById(establishmentId)
                .orElseThrow(() -> new EntityNotFoundException("Estabelecimento não encontrado"));

        TableModel table = new TableModel();
        table.setNumber(number);
        table.setStatus(TableStatusEnum.OPEN); // se for enum melhor ainda
        table.setCreatedAt(LocalDateTime.now());
        table.setEstablishment(establishment);

        return tableRepository.save(table);
    }

    public TableModel update(UUID id, Integer number) {

        TableModel table = findById(id);
        table.setNumber(number);

        return tableRepository.save(table);
    }

    public TableModel attachCard(UUID tableId, UUID cardId) {

        TableModel table = findById(tableId);

        CardModel card = cardRepository.findById(cardId)
                .orElseThrow(() -> new EntityNotFoundException("Cartão não encontrado"));

        table.setCard(card);

        return tableRepository.save(table);
    }

    public void delete(UUID tableId) {

        if (!tableRepository.existsById(tableId)) {
            throw new EntityNotFoundException("Mesa não encontrada");
        }

        tableRepository.deleteById(tableId);
    }
}