package com.lucasmaciel404.pdv_api.service;

import com.lucasmaciel404.pdv_api.model.Establishment;
import com.lucasmaciel404.pdv_api.repository.EstablishmentRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class EstablishmentService {

    private final EstablishmentRepository establishmentRepository;

    public EstablishmentService(EstablishmentRepository establishmentRepository) {
        this.establishmentRepository = establishmentRepository;
    }

    // CREATE
    public Establishment create(Establishment establishment) {
        establishment.setActive(true);

        return establishmentRepository.save(establishment);
    }

    // FIND ALL
    public List<Establishment> findAll() {
        return establishmentRepository.findAll();
    }

    // FIND BY ID
    public Establishment findById(UUID id) {
        return establishmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Establishment not found"));
    }

    // UPDATE
    public Establishment update(UUID id, Establishment updatedData) {
        Establishment establishment = findById(id);

        establishment.setName(updatedData.getName());
        establishment.setActive(updatedData.getActive());

        return establishmentRepository.save(establishment);
    }

    // DELETE (soft delete recomendado)
    public void delete(UUID id) {
        Establishment establishment = findById(id);
        establishment.setActive(false);
        establishmentRepository.save(establishment);
    }
}