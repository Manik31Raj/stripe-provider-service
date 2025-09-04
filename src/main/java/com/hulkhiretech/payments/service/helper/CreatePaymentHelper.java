package com.hulkhiretech.payments.service.helper;

import com.hulkhiretech.payments.constant.Constant;
import com.hulkhiretech.payments.http.HttpRequest;
import com.hulkhiretech.payments.pojo.CreatePaymentRequest;
import com.hulkhiretech.payments.pojo.LineItem;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@Service
@Slf4j
public class CreatePaymentHelper {

    @Value("${stripe.api.key}")
    private String  stripeAPIKey;
    @Value("${stripe.create-session-url}")
    private String createSessionUrl;

    public CreatePaymentHelper() {
    }


    public HttpRequest prepareHttpRequest(CreatePaymentRequest createPaymentRequest) {

        log.info("Preparing HTTP request for payment creation: {}", createPaymentRequest);
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(stripeAPIKey, Constant.EMPTY_STRING);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);


        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add(Constant.MODE, Constant.MODE_PAYMENT);
        requestBody.add(Constant.SUCCESS_URL, createPaymentRequest.getSuccessUrl());
        requestBody.add(Constant.CANCEL_URL, createPaymentRequest.getCancelUrl());


        for(int i = 0; i< createPaymentRequest.getLineItems().size(); i++) {
            LineItem lineItem = createPaymentRequest.getLineItems().get(i);

            requestBody.add("line_items["+i+"][price_data][currency]", String.valueOf(lineItem.getCurrency()));
            requestBody.add("line_items["+i+"][price_data][unit_amount]", String.valueOf((int)(lineItem.getUnitAmount()))); // amount in cents
            requestBody.add("line_items["+i+"][price_data][product_data][name]", lineItem.getProductName());
            requestBody.add("line_items["+i+"][price_data][product_data][images][]", lineItem.getProductImageUrl());
            requestBody.add("line_items["+i+"][price_data][product_data][description]", lineItem.getProductDescription());
            requestBody.add("line_items["+i+"][quantity]", String.valueOf(lineItem.getQuantity()));
        }
        requestBody.add("invoice_creation[enabled]", String.valueOf(Constant.INVOICE_CREATION_ENABLED));

        HttpRequest httpRequest = new HttpRequest();
        httpRequest.setHttpMethod(HttpMethod.POST);
        httpRequest.setUrl(createSessionUrl);
        httpRequest.setHttpHeaders(headers);
        httpRequest.setRequestBody(requestBody);

        log.info("Prepared HTTP request: {}", httpRequest);

        return httpRequest;
    }
}
