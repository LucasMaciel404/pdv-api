package com.lucasmaciel404.pdv_api.service;

import com.lucasmaciel404.pdv_api.model.CardModel;
import com.lucasmaciel404.pdv_api.model.TableModel;
import com.lucasmaciel404.pdv_api.repository.CardRepository;
import com.lucasmaciel404.pdv_api.repository.TableRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class TableService {

    private final TableRepository tableRepository;
    private final CardRepository cardRepository;

    public TableService(TableRepository tableRepository,
                        CardRepository cardRepository) {
        this.tableRepository = tableRepository;
        this.cardRepository = cardRepository;
    }

    // 🔎 Buscar por CardId
    public TableModel findByCardId(UUID cardId) {
        return tableRepository.findByCardId(cardId)
                .orElseThrow(() -> new EntityNotFoundException("Mesa não encontrada para esse cartão"));
    }

    // 📋 Listar todas
    public List<TableModel> findAll() {
        return tableRepository.findAll();
    }

    // ➕ Criar mesa
    public TableModel create(Integer number, UUID cardId) {
        CardModel card = null;

        if (cardId != null) {
            card = cardRepository.findById(cardId)
                    .orElseThrow(() -> new EntityNotFoundException("Cartão não encontrado"));
        }

        TableModel table = new TableModel();
        table.setNumber(number);
        table.setCard(card);

        return tableRepository.save(table);
    }

    // ❌ Deletar
    public void delete(UUID tableId) {
        if (!tableRepository.existsById(tableId)) {
            throw new EntityNotFoundException("Mesa não encontrada");
        }

        tableRepository.deleteById(tableId);
    }
}
