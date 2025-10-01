package com.hulkhiretech.payments.service.helper;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import com.hulkhiretech.payments.constant.NotificationType;
import com.hulkhiretech.payments.http.HttpRequest;
import com.hulkhiretech.payments.processingservice.NotificationRequest;
import com.hulkhiretech.payments.stripe.StripeResponse;
import com.hulkhiretech.payments.util.JsonUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class StripeWebhookHelper {

    private static final String STRIPE = "STRIPE";

    @Value("${processing.notification.url}")
    private String processingNotificationUrl;

    private final JsonUtil jsonUtil;


    public HttpRequest prepareSuccessNotificationRequest(StripeResponse response) {
        log.info("Preparing success notification request for stripeResponse: {}", response);

        HttpHeaders httpHeaders	 = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);

        NotificationRequest reqObj = new NotificationRequest();
        reqObj.setNotificationType(NotificationType.PAYMENT_SUCCESS.name());
        reqObj.setProvider(STRIPE);
        reqObj.setProviderReference(response.getId());

        String reqAsJson = jsonUtil.convertObjectToJson(reqObj);
        log.info("Converted CreatePaymentRequest to JSON: {}", reqAsJson);

        HttpRequest httpRequest = new HttpRequest();
        httpRequest.setHttpMethod(HttpMethod.POST);
        httpRequest.setUrl(processingNotificationUrl);
        httpRequest.setHttpHeaders(httpHeaders);
        httpRequest.setRequestBody(reqAsJson);

        log.info("Prepared HttpRequest: {}", httpRequest);
        return httpRequest;
    }

    public HttpRequest prepareFailureNotificationRequest(StripeResponse response) {
        log.info("Preparing failed notification request for providerReference: {}",
                response);

        HttpHeaders httpHeaders	 = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);

        NotificationRequest reqObj = new NotificationRequest();
        reqObj.setNotificationType(NotificationType.PAYMENT_FAILED.name());
        reqObj.setProvider(STRIPE);
        reqObj.setProviderReference(response.getId());


        Map<String, String> payload = new HashMap<>();
        payload.put("errorCode", "123");
        payload.put("errorMessage", "Payment failed due to XYZ reason");
        reqObj.setPayload(payload);


        String reqAsJson = jsonUtil.convertObjectToJson(reqObj);
        log.info("Converted CreatePaymentRequest to JSON: {}", reqAsJson);

        HttpRequest httpRequest = new HttpRequest();
        httpRequest.setHttpMethod(HttpMethod.POST);
        httpRequest.setUrl(processingNotificationUrl);
        httpRequest.setHttpHeaders(httpHeaders);
        httpRequest.setRequestBody(reqAsJson);

        log.info("Prepared HttpRequest: {}", httpRequest);
        return httpRequest;
    }
}
