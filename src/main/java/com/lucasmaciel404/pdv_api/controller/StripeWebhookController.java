package com.lucasmaciel404.pdv_api.controller;

import com.lucasmaciel404.pdv_api.model.UserModel;
import com.lucasmaciel404.pdv_api.repository.UserRepository;
import com.stripe.model.Event;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.stripe.model.Invoice;

@RestController
@RequestMapping("/stripe")
@RequiredArgsConstructor
public class StripeWebhookController {

    private final UserRepository userRepository;

    private final String endpointSecret = "whsec_XXXX"; // pegar no stripe

    @PostMapping("/webhook")
    public ResponseEntity<String> handleWebhook(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String sigHeader
    ) {

        try {
            Event event = Webhook.constructEvent(payload, sigHeader, endpointSecret);

            switch (event.getType()) {

                case "checkout.session.completed":

                    Session session = (Session) event.getDataObjectDeserializer()
                            .getObject().get();

                    String customerId = session.getCustomer();
                    String subscriptionId = session.getSubscription();

                    UserModel user = userRepository
                            .findByStripeCustomerId(customerId)
                            .orElseThrow();

                    user.setSubscriptionId(subscriptionId);
                    user.setActive(true);

                    userRepository.save(user);

                    break;

                case "invoice.payment_failed":

                    Invoice invoice = (Invoice) event.getDataObjectDeserializer()
                            .getObject().get();

                    String customerIdFailed = invoice.getCustomer();

                    UserModel userFailed = userRepository
                            .findByStripeCustomerId(customerIdFailed)
                            .orElseThrow();

                    userFailed.setActive(false);

                    userRepository.save(userFailed);

                    break;
            }

            return ResponseEntity.ok().build();

        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}