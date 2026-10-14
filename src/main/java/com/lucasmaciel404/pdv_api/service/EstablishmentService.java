package com.lucasmaciel404.pdv_api.service;

import com.lucasmaciel404.pdv_api.dto.enums.model.UserRoleEnum;
import com.lucasmaciel404.pdv_api.model.Establishment;
import com.lucasmaciel404.pdv_api.model.UserEstablishment;
import com.lucasmaciel404.pdv_api.model.UserModel;
import com.lucasmaciel404.pdv_api.repository.EstablishmentRepository;
import com.lucasmaciel404.pdv_api.repository.UserEstablishmentRepository;
import com.lucasmaciel404.pdv_api.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EstablishmentService {

    private final EstablishmentRepository establishmentRepository;
    private final UserEstablishmentRepository userEstablishmentRepository;
    private final UserRepository userRepository;
    private final EstablishmentJoinRequestService joinRequestService;

    public Establishment create(Establishment establishment, String gmail) {
        UserModel user = userRepository.findByEmail(gmail).orElseThrow();

        if (!Boolean.TRUE.equals(user.getSubscriptionActive())) {
            return null;
        }

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

    @Transactional
    public UserEstablishment setUserToEstablishment(
            UUID userId,
            UUID establishmentId,
            String role
    ) {
        UserModel user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new IllegalArgumentException("User not found"));

        Establishment establishment = establishmentRepository.findById(establishmentId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Establishment not found"));

        UserRoleEnum userRole;

        try {
            userRole = UserRoleEnum.valueOf(role);
        } catch (IllegalArgumentException | NullPointerException e) {
            throw new IllegalArgumentException("Invalid user role.");
        }

        if (userRole == UserRoleEnum.OWNER) {
            boolean alreadyOwner =
                    userEstablishmentRepository
                            .existsByUserIdAndEstablishmentIdAndRole(
                                    userId,
                                    establishmentId,
                                    UserRoleEnum.OWNER
                            );

            if (alreadyOwner) {
                throw new IllegalArgumentException(
                        "User is already an owner of this establishment."
                );
            }
        }

        if (userRole == UserRoleEnum.WAITER) {
            boolean requestAccepted =
                    joinRequestService.hasAcceptedRequest(
                            establishmentId,
                            userId
                    );

            if (!requestAccepted) {
                throw new IllegalArgumentException(
                        "An accepted join request is required for the WAITER role."
                );
            }

            boolean alreadyLinked =
                    userEstablishmentRepository
                            .existsByUserIdAndEstablishmentIdAndRole(
                                    userId,
                                    establishmentId,
                                    UserRoleEnum.WAITER
                            );

            if (alreadyLinked) {
                throw new IllegalArgumentException(
                        "User is already a waiter at this establishment."
                );
            }
        }

        UserEstablishment userEstablishment = new UserEstablishment();
        userEstablishment.setUser(user);
        userEstablishment.setEstablishment(establishment);
        userEstablishment.setRole(userRole);

        return userEstablishmentRepository.save(userEstablishment);
    }

    public UserEstablishment getEstablishWithUserId(UUID userId) {
        return userEstablishmentRepository
                .findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}