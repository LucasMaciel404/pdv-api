package com.lucasmaciel404.pdv_api.service;

import com.lucasmaciel404.pdv_api.dto.enums.model.UserRoleEnum;
import com.lucasmaciel404.pdv_api.model.Establishment;
import com.lucasmaciel404.pdv_api.model.UserEstablishment;
import com.lucasmaciel404.pdv_api.model.UserModel;
import com.lucasmaciel404.pdv_api.repository.EstablishmentRepository;
import com.lucasmaciel404.pdv_api.repository.UserEstablishmentRepository;
import com.lucasmaciel404.pdv_api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EstablishmentService {

    private final EstablishmentRepository establishmentRepository;
    private final UserEstablishmentRepository userEstablishmentRepository;
    private final UserRepository userRepository;

    public Establishment create(Establishment establishment) {
        establishment.setActive(true);

        return establishmentRepository.save(establishment);
    }

    public List<Establishment> findAll() {
        return establishmentRepository.findAll();
    }

    public Establishment findById(UUID id) {
        return establishmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Establishment not found"));
    }

    public Establishment update(UUID id, Establishment updatedData) {
        Establishment establishment = findById(id);

        establishment.setName(updatedData.getName());
        establishment.setActive(updatedData.getActive());

        return establishmentRepository.save(establishment);
    }

    public void delete(UUID id) {
        Establishment establishment = findById(id);
        establishment.setActive(false);
        establishmentRepository.save(establishment);
    }

    public ResponseEntity<?> setUserToEstablishment(UUID userId, UUID establishmentId, String role) {
        UserModel user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        Establishment establishment = establishmentRepository.findById(establishmentId).orElseThrow(() -> new RuntimeException("Establishment not found"));

        UserEstablishment userEstablishment = new UserEstablishment();
        userEstablishment.setUser(user);
        userEstablishment.setEstablishment(establishment);
        userEstablishment.setRole(UserRoleEnum.valueOf(role));
        userEstablishmentRepository.save(userEstablishment);
        return ResponseEntity.ok(userEstablishment);
    }
}