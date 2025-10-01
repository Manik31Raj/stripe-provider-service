package com.hulkhiretech.payments.controller;

import com.hulkhiretech.payments.service.interfaces.StripeWebhookService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/stripe/webhook")
@Slf4j
@RequiredArgsConstructor
public class StripeWebhookController {

    private final StripeWebhookService stripeWebhookService;

    @PostMapping
    public String handleStripeWebhook(@RequestHeader("Stripe-Signature") String sigHeader, @RequestBody String jsonPayload) {
        log.info("Received Stripe webhook || sigHeader : {} | jsonPayLoad : {}", sigHeader, jsonPayload);

        String response=stripeWebhookService.handleStripeWebhook(sigHeader, jsonPayload);
        log.info("Response from service: {}", response);

        return response;
    }
}
