package com.lucasmaciel404.pdv_api.controller;

import com.lucasmaciel404.pdv_api.model.UserModel;
import com.lucasmaciel404.pdv_api.repository.UserRepository;
import com.stripe.model.Event;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.stripe.model.Invoice;
import com.stripe.model.StripeObject;

@RestController
@RequestMapping("/stripe")
@RequiredArgsConstructor
public class StripeWebhookController {

    private final UserRepository userRepository;

    @Value("${stripe.webhook.secret}")
    private String endpointSecret;

    @PostMapping("/webhook")
    public ResponseEntity<String> handleWebhook(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String sigHeader
    ) {

        try {
            Event event = Webhook.constructEvent(payload, sigHeader, endpointSecret);

            System.out.println("Evento recebido: " + event.getType());

            var optionalObject = event.getDataObjectDeserializer().getObject();

            switch (event.getType()) {

                case "checkout.session.completed":

                    System.out.println("checkout.session.completed");

                    Session session;
                    if (optionalObject.isPresent()) {
                        session = (Session) optionalObject.get();
                    } else {
                        session = (Session) event.getDataObjectDeserializer().deserializeUnsafe();
                    }

                    String customerId = session.getCustomer();
                    String subscriptionId = session.getSubscription();

                    userRepository.findByStripeCustomerId(customerId).ifPresent(user -> {
                        user.setSubscriptionId(subscriptionId);
                        user.setSubscriptionActive(true);
                        userRepository.save(user);
                    });

                    break;


                case "invoice.paid":

                    System.out.println("invoice.paid");

                    Invoice invoicePaid;
                    if (optionalObject.isPresent()) {
                        invoicePaid = (Invoice) optionalObject.get();
                    } else {
                        invoicePaid = (Invoice) event.getDataObjectDeserializer().deserializeUnsafe();
                    }

                    String customerIdPaid = invoicePaid.getCustomer();

                    userRepository.findByStripeCustomerId(customerIdPaid).ifPresent(user -> {
                        user.setSubscriptionActive(true);
                        userRepository.save(user);
                    });

                    break;


                case "invoice.payment_failed":

                    System.out.println("invoice.payment_failed");

                    Invoice invoiceFailed;
                    if (optionalObject.isPresent()) {
                        invoiceFailed = (Invoice) optionalObject.get();
                    } else {
                        invoiceFailed = (Invoice) event.getDataObjectDeserializer().deserializeUnsafe();
                    }

                    String customerIdFailed = invoiceFailed.getCustomer();

                    userRepository.findByStripeCustomerId(customerIdFailed).ifPresent(user -> {
                        user.setSubscriptionActive(false);
                        userRepository.save(user);
                    });

                    break;


                case "customer.subscription.deleted":

                    System.out.println("customer.subscription.deleted");

                    com.stripe.model.Subscription subDeleted;
                    if (optionalObject.isPresent()) {
                        subDeleted = (com.stripe.model.Subscription) optionalObject.get();
                    } else {
                        subDeleted = (com.stripe.model.Subscription) event.getDataObjectDeserializer().deserializeUnsafe();
                    }

                    String customerIdSub = subDeleted.getCustomer();

                    userRepository.findByStripeCustomerId(customerIdSub).ifPresent(user -> {
                        user.setSubscriptionActive(false);
                        user.setSubscriptionId(null);
                        userRepository.save(user);
                    });

                    break;


                case "customer.subscription.updated":

                    System.out.println("customer.subscription.updated");

                    com.stripe.model.Subscription subUpdated;
                    if (optionalObject.isPresent()) {
                        subUpdated = (com.stripe.model.Subscription) optionalObject.get();
                    } else {
                        subUpdated = (com.stripe.model.Subscription) event.getDataObjectDeserializer().deserializeUnsafe();
                    }

                    String customerIdUpdated = subUpdated.getCustomer();

                    boolean isActive = "active".equals(subUpdated.getStatus());

                    userRepository.findByStripeCustomerId(customerIdUpdated).ifPresent(user -> {
                        user.setSubscriptionActive(isActive);
                        userRepository.save(user);
                    });

                    break;


                default:
                    System.out.println("Evento ignorado: " + event.getType());
                    break;
            }

            return ResponseEntity.ok().build();

        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}