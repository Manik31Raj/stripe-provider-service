package com.hulkhiretech.payments.service.impl;

import com.hulkhiretech.payments.constant.ErrorCodeEnum;
import com.hulkhiretech.payments.exception.StripeProviderException;
import com.hulkhiretech.payments.http.HttpRequest;
import com.hulkhiretech.payments.http.HttpServiceEngine;
import com.hulkhiretech.payments.pojo.CreatePaymentRequest;
import com.hulkhiretech.payments.pojo.PaymentResponse;
import com.hulkhiretech.payments.service.helper.CreatePaymentHelper;
import com.hulkhiretech.payments.service.interfaces.PaymentService;
import com.hulkhiretech.payments.util.JsonUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final HttpServiceEngine httpServiceEngine;

    private final JsonUtil jsonUtil;

    private final CreatePaymentHelper createPaymentHelper;

    private final ChatClient chatClient;
    @Override
    public PaymentResponse cratePayment(CreatePaymentRequest createPaymentRequest) {
        log.info("Processing payment creation: {}", createPaymentRequest);

        if(createPaymentRequest.getLineItems().get(0).getQuantity()<=0) {
            throw new StripeProviderException(
                    ErrorCodeEnum.INVAlID_QUANTITY.getErrorCode(),
                    ErrorCodeEnum.INVAlID_QUANTITY.getErrorMessage(),
                    HttpStatus.BAD_REQUEST
            );
        }

        HttpRequest httpRequest = createPaymentHelper.prepareHttpRequest(createPaymentRequest);

        ResponseEntity<String> httpResponse=httpServiceEngine.makeHttpCall(httpRequest);
        log.info("HTTP call response: {}", httpResponse);

        PaymentResponse response = processResponse(httpResponse);

        log.info("Final Payment creation to be returned : {}", response);

        return response;
    }

    private PaymentResponse processResponse(ResponseEntity<String> httpResponse) {

        log.info("Processing HTTP response | httpResponse : {}", httpResponse);

        if (httpResponse.getStatusCode().is2xxSuccessful()) {
            log.info("HTTP response is successful");
            PaymentResponse response = jsonUtil.convertJsonToObject(httpResponse.getBody(), PaymentResponse.class);
            log.info("Parsed PaymentResponse: {}", response);

            if(response != null && response.getId() != null && response.getUrl() != null) {
                log.info("PaymentResponse is valid and contains required fields");
                return response ;
            }

        }
        if(httpResponse.getStatusCode().is4xxClientError()) {
            log.error("Client error occurred with status code: {}", httpResponse.getStatusCode());

            String errorMessage = prepareErrorSummaryMessage(httpResponse);
            log.error("Generated error summary message: {}", errorMessage);

            throw new StripeProviderException(
                    ErrorCodeEnum.STRIPE_ERROR.getErrorCode(),
                    errorMessage,
                    HttpStatus.valueOf(httpResponse.getStatusCode().value())
            );
        } else if(httpResponse.getStatusCode().is5xxServerError()) {
            log.error("Server error occurred with status code: {}", httpResponse.getStatusCode());
            throw new StripeProviderException(
                    ErrorCodeEnum.UNABLE_TO_CONNECT_TO_STRIPE.getErrorCode(),
                    ErrorCodeEnum.UNABLE_TO_CONNECT_TO_STRIPE.getErrorMessage(),
                    HttpStatus.SERVICE_UNAVAILABLE
            );
        }

        throw new StripeProviderException(
                ErrorCodeEnum.PAYMENT_CREATION_FAILED.getErrorCode(),
                ErrorCodeEnum.PAYMENT_CREATION_FAILED.getErrorMessage(),
                HttpStatus.SERVICE_UNAVAILABLE
        );
    }
    private String prepareErrorSummaryMessage(ResponseEntity<String> httpResponse) {
        String promptTemplate = """
                Given the following json message from a third-party API, read the entire JSON, and summarize in 1 line:
                Instructions:
                1. Put a short, simple summary. Which exactly represents what error happened.
                2. Max length of summary less than 100 characters.
                3. Keep the output clear and concise.
                4. Summarize as message that we can send in API response to the client.
                5. Dont point any info to read external documentation or link.
                {error_json}
                """;

        String errorJson = httpResponse.getBody();

        String response = chatClient
                .prompt()
                .system("You are a error handling specialist. according to the error fetched from third party API, you will summarize it in 10-15 words maximum.")
                .user(PromptUserSpec -> PromptUserSpec
                        .text(promptTemplate)
                        .param("error_json", errorJson))
                .call()
                .content();

        log.info("AI Model response: {}", response);
        return response;
    }
}
