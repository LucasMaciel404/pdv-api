package com.lucasmaciel404.pdv_api.security;

import com.stripe.Stripe;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StripeConfig {

    @PostConstruct
    public void init() {
        Stripe.apiKey = "sk_test_...";
    }
}