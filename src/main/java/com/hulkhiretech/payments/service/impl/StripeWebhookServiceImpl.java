package com.hulkhiretech.payments.service.impl;

import com.hulkhiretech.payments.constant.ErrorCodeEnum;
import com.hulkhiretech.payments.exception.StripeProviderException;
import com.hulkhiretech.payments.service.interfaces.StripeWebhookService;
import com.hulkhiretech.payments.service.ProcessStripeEventAsync;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import com.stripe.net.Webhook;
import com.stripe.model.Event;

@Service
@Slf4j
@RequiredArgsConstructor
public class StripeWebhookServiceImpl implements StripeWebhookService {

    @Value("${stripe.webhook.secret}")
    private String endpointSecret;

    private final ProcessStripeEventAsync processStripeEventAsync;

    @Override
    public String handleStripeWebhook(String sigHeader,String jsonPayload) {
        log.info("Received Stripe webhook || jsonPayLoad : {}", jsonPayload);

        Event event = checkSignValid(sigHeader, jsonPayload);

        log.info("HmacSHA256 Signature is valid. Now Processing event type: {}", event.getType());

        processStripeEventAsync.processStripeEvent(event);

        return "Stripe Webhook Processed";
    }

    private Event checkSignValid(String sigHeader, String jsonPayload) {

        try{
            Event event=Webhook.constructEvent(
                    jsonPayload, sigHeader, endpointSecret
            );
            log.info("Stripe signature valid for Event : {}", event);
            return event;
        }catch(Exception e)
        {
            log.error("⚠️  Webhook error while parsing basic request. {}", e.getMessage());
            throw new StripeProviderException(
                    ErrorCodeEnum.INVALID_STRIPE_SIGNATURE.getErrorCode(),
                    ErrorCodeEnum.INVALID_STRIPE_SIGNATURE.getErrorMessage(),
                    HttpStatus.BAD_REQUEST
            );
        }
    }
}
