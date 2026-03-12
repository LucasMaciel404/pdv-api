package com.lucasmaciel404.pdv_api.service;

import com.lucasmaciel404.pdv_api.dto.request.CardRequest;
import com.lucasmaciel404.pdv_api.model.CardModel;
import com.lucasmaciel404.pdv_api.model.Establishment;
import com.lucasmaciel404.pdv_api.repository.CardRepository;
import com.lucasmaciel404.pdv_api.repository.EstablishmentRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.function.EntityResponse;

import javax.smartcardio.Card;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CardService {
    private final CardRepository cardRepository;
    private final EstablishmentRepository establishmentRepository;

    public CardModel createCard(CardRequest card) {
        CardModel newCard = new CardModel();

        newCard.setCardCode(card.cardId());
        newCard.setActive(card.active());

        Optional<Establishment> establishment = establishmentRepository.findById(card.estabilishment());

        if (establishment.isPresent()) {
            newCard.setEstablishment(establishment.get());
        }

        return cardRepository.save(newCard);
    }
    public CardModel getCardWithCardCode(String cardCode) {
        return cardRepository
                .findByCardCode(cardCode)
                .orElseThrow(() -> new EntityNotFoundException("Card not found"));
    }

}
