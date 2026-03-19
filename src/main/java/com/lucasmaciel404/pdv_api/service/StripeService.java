package com.lucasmaciel404.pdv_api.service;

import com.lucasmaciel404.pdv_api.model.UserModel;
import com.lucasmaciel404.pdv_api.repository.UserRepository;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StripeService {

    private final UserRepository userRepository;

    public String createCheckoutSession(String email, String priceId) {

        UserModel user = userRepository.findByEmail(email).orElseThrow();

        try {
            SessionCreateParams params =
                    SessionCreateParams.builder()
                            .setMode(SessionCreateParams.Mode.SUBSCRIPTION)
                            .setCustomer(user.getStripeCustomerId())
                            .addLineItem(
                                    SessionCreateParams.LineItem.builder()
                                            .setPrice(priceId)
                                            .setQuantity(1L)
                                            .build()
                            )
                            .setSuccessUrl("cardorder://success")
                            .setCancelUrl("cardorder://cancel")
                            .build();

            Session session = Session.create(params);

            return session.getId();

        } catch (Exception e) {
            throw new RuntimeException("Erro ao criar checkout", e);
        }
    }
}