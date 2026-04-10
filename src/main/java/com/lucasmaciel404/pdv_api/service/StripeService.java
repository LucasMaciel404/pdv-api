package com.lucasmaciel404.pdv_api.service;

import com.lucasmaciel404.pdv_api.model.UserModel;
import com.lucasmaciel404.pdv_api.repository.UserRepository;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class StripeService {

    private final UserRepository userRepository;

    public String createCheckoutSession(String email, String priceId) {

        UserModel user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        try {

            // 🔒 1. Impede múltiplas assinaturas
            if (Boolean.TRUE.equals(user.getSubscriptionActive())) {
                throw new RuntimeException("Usuário já possui assinatura ativa");
            }

            // 🔒 2. Garante que tem customer no Stripe
            String customerId = user.getStripeCustomerId();

            if (customerId == null || customerId.isEmpty()) {
                throw new RuntimeException("Usuário sem customerId no Stripe");
            }

            // 🔥 3. Cria sessão
            SessionCreateParams params =
                    SessionCreateParams.builder()
                            .setMode(SessionCreateParams.Mode.SUBSCRIPTION)
                            .setCustomer(customerId)

                            // 🔥 metadata (muito útil no webhook)
                            .putMetadata("userId", user.getId().toString())

                            .addLineItem(
                                    SessionCreateParams.LineItem.builder()
                                            .setPrice(priceId)
                                            .setQuantity(1L)
                                            .build()
                            )

                            // 🔗 deep linking
                            .setSuccessUrl("cardorder://success")
                            .setCancelUrl("cardorder://cancel")

                            .build();

            Session session = Session.create(params);

            return session.getUrl();

        } catch (Exception e) {
            throw new RuntimeException("Erro ao criar checkout", e);
        }
    }
    public String createPortalSession(String email) {

        UserModel user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        String customerId = user.getStripeCustomerId();

        if (customerId == null || customerId.isEmpty()) {
            throw new RuntimeException("Usuário sem customerId no Stripe");
        }

        try {
            Map<String, Object> params = new HashMap<>();
            params.put("customer", customerId);
            params.put("return_url", "cardorder://(tabs)/ ");

            com.stripe.model.billingportal.Session session =
                    com.stripe.model.billingportal.Session.create(params);

            return session.getUrl();

        } catch (Exception e) {
            throw new RuntimeException("Erro ao criar portal", e);
        }
    }
}