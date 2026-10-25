package com.lucasmaciel404.pdv_api.service;

import com.lucasmaciel404.pdv_api.dto.enums.model.UserRoleEnum;
import com.lucasmaciel404.pdv_api.dto.request.CardRequest;
import com.lucasmaciel404.pdv_api.model.CardModel;
import com.lucasmaciel404.pdv_api.model.Establishment;
import com.lucasmaciel404.pdv_api.model.UserEstablishment;
import com.lucasmaciel404.pdv_api.model.UserModel;
import com.lucasmaciel404.pdv_api.repository.CardRepository;
import com.lucasmaciel404.pdv_api.repository.EstablishmentRepository;
import com.lucasmaciel404.pdv_api.repository.UserEstablishmentRepository;
import com.lucasmaciel404.pdv_api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CardService {

    private final CardRepository cardRepository;
    private final EstablishmentRepository establishmentRepository;
    private final UserEstablishmentRepository userEstablishmentRepository;
    private final UserRepository userRepository;

    private UserEstablishment requireOwner(String email, UUID establishmentId) {
        UserModel user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Usuário não encontrado"
                ));

        UserEstablishment link = userEstablishmentRepository
                .findByUserId(user.getId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.FORBIDDEN,
                        "Usuário não possui vínculo com um estabelecimento"
                ));

        if (link.getRole() != UserRoleEnum.OWNER) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Somente o proprietário pode gerenciar cartões"
            );
        }

        if (!link.getEstablishment().getId().equals(establishmentId)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Você não pode acessar outro estabelecimento"
            );
        }

        return link;
    }

    @Transactional
    public CardModel createCard(CardRequest request, String email) {
        Establishment establishment = establishmentRepository
                .findById(request.estabilishment())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Estabelecimento não encontrado"
                ));

        requireOwner(email, establishment.getId());

        Optional<CardModel> existingCard =
                cardRepository.findByCardCodeAndEstablishmentId(
                        request.cardId(),
                        establishment.getId()
                );

        if (existingCard.isPresent()) {
            CardModel card = existingCard.get();

            if (Boolean.TRUE.equals(card.getActive())) {
                throw new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "Este cartão já está cadastrado neste estabelecimento"
                );
            }

            card.setActive(true);
            return cardRepository.save(card);
        }

        CardModel newCard = new CardModel();
        newCard.setCardCode(request.cardId());
        newCard.setActive(true);
        newCard.setEstablishment(establishment);

        return cardRepository.save(newCard);
    }

    @Transactional
    public void deleteCard(UUID cardId, String email) {
        UserModel user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Usuário não encontrado"
                ));

        UserEstablishment link = userEstablishmentRepository
                .findByUserId(user.getId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.FORBIDDEN,
                        "Usuário não possui vínculo com um estabelecimento"
                ));

        if (link.getRole() != UserRoleEnum.OWNER) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Somente o proprietário pode desativar cartões"
            );
        }

        CardModel card = cardRepository
                .findByIdAndEstablishmentId(
                        cardId, link.getEstablishment().getId()
                )
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Cartão não encontrado"
                ));

        if (Boolean.FALSE.equals(card.getActive())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Este cartão já está desativado"
            );
        }

        card.setActive(false);
        cardRepository.save(card);
    }
    @Transactional(readOnly = true)
    public List<CardModel> getCardsByEstablishment(String email) {
        UserModel user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Usuário não encontrado"
                ));

        UserEstablishment link = userEstablishmentRepository
                .findByUserId(user.getId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.FORBIDDEN,
                        "Usuário não possui vínculo com um estabelecimento"
                ));

        if (link.getRole() != UserRoleEnum.OWNER) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Somente o proprietário pode consultar cartões"
            );
        }

        return cardRepository.findByEstablishmentId(
                link.getEstablishment().getId()
        );
    }
}