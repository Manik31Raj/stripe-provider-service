package com.hulkhiretech.payments.service;

import com.hulkhiretech.payments.http.HttpRequest;
import com.hulkhiretech.payments.http.HttpServiceEngine;
import com.hulkhiretech.payments.service.helper.StripeWebhookHelper;
import com.hulkhiretech.payments.stripe.StripeResponse;
import com.hulkhiretech.payments.util.JsonUtil;
import com.stripe.model.Event;
import com.stripe.model.EventDataObjectDeserializer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProcessStripeEventAsync {

    private final HttpServiceEngine httpServiceEngine;

    private final StripeWebhookHelper stripeWebhookHelper;

    private final JsonUtil jsonUtil;

    private final List<String> successEvents = List.of(
            "checkout.session.completed",
            "checkout.session.async_payment_succeeded"
    );

    private final List<String> failedEvents = List.of(
            "checkout.session.async_payment_failed"
    );

    @Async
    public void processStripeEvent(Event event) {

        log.info("Received Stripe event : {}", event.getType());

        if (!successEvents.contains(event.getType()) && !failedEvents.contains(event.getType()))
        {
            log.info("Ignoring event type: {}", event.getType());
            return;
        }

        log.info("Processing incoming checkout session event : {}", event.getType());

        EventDataObjectDeserializer objDeserializer=event.getDataObjectDeserializer();
        log.info("Event Data Object Deserializer: {}", objDeserializer);

        String eventAsJson=objDeserializer.getRawJson();
        log.info("Event as JSON: {}", eventAsJson);

        StripeResponse response=jsonUtil.convertJsonToObject(eventAsJson, StripeResponse.class);
        log.info("Mapped Stripe Response: {}", response);

        if(successEvents.contains(event.getType()))
        {
            if(response.getPaymentStatus().equals("paid")){
                triggerSuccessNotifications(response);
            }else{
                log.warn("Payment not completed yet for eventType : {}",event.getType());
            }
            log.info("Payment succeeded for session id: {}", response.getId());
            return;
        }

        if(failedEvents.contains(event.getType()))
        {
            triggerFailedNotifications(response);
            log.info("Payment failed for session id: {}", response.getId());
        }


        log.info("Complete Processing Stripe Eevent: {}", event.getId());
    }

    private void triggerFailedNotifications(StripeResponse response) {
        HttpRequest httpRequest=stripeWebhookHelper.prepareFailureNotificationRequest(response);

        log.info("Prepared HttpRequest for failure notification: {}", httpRequest);

        ResponseEntity<String> notificationResponse= httpServiceEngine.makeHttpCall(httpRequest);
        log.info("Notification service response: {}", notificationResponse);

        if(notificationResponse.getStatusCode().is2xxSuccessful())
        {
            log.info("Successfully sent FAILURE notification to processing-service");
        } else {
            log.error("Failed to send FAILURE notification to processing-service. Response: {}", notificationResponse);
        }
    }

    private void triggerSuccessNotifications(StripeResponse response) {
        HttpRequest httpRequest=stripeWebhookHelper.prepareSuccessNotificationRequest(response);

        log.info("Prepared HttpRequest for success notification: {}", httpRequest);

        ResponseEntity<String> notificationResponse= httpServiceEngine.makeHttpCall(httpRequest);
        log.info("Notification service response: {}", notificationResponse);

        if(notificationResponse.getStatusCode().is2xxSuccessful())
        {
            log.info("Successfully sent SUCCESS notification to processing-service");
        } else {
            log.error("Failed to send SUCCESS notification to processing-service. Response: {}", notificationResponse);
        }
    }
}
