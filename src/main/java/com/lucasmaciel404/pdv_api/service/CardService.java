package com.lucasmaciel404.pdv_api.service;

import com.lucasmaciel404.pdv_api.dto.request.CardRequest;
import com.lucasmaciel404.pdv_api.model.CardModel;
import com.lucasmaciel404.pdv_api.repository.CardRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.function.EntityResponse;

import javax.smartcardio.Card;

@Service
@RequiredArgsConstructor
public class CardService {
    private final CardRepository cardRepository;

    public CardModel createCard(CardRequest card) {
        CardModel newCard = new CardModel();
        newCard.setCardCode(card.cardId());
        newCard.setActive(card.active());
        return cardRepository.save(newCard);
    }
    public CardModel getCardWithCardCode(String cardCode) {
        return cardRepository
                .findByCardCode(cardCode)
                .orElseThrow(() -> new EntityNotFoundException("Card not found"));
    }

}
