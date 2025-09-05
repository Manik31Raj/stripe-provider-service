package com.hulkhiretech.payments.service.helper;

import com.hulkhiretech.payments.constant.Constant;
import com.hulkhiretech.payments.constant.ErrorCodeEnum;
import com.hulkhiretech.payments.exception.StripeProviderException;
import com.hulkhiretech.payments.http.HttpRequest;
import com.hulkhiretech.payments.stripe.StripeResponse;
import com.hulkhiretech.payments.util.JsonUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExpirePaymentHelper {

    @Value("${stripe.api.key}")
    private String  stripeAPIKey;

    @Value("${stripe.expire-session-url}")
    private String getExpireUrlTemplate;

    private final JsonUtil jsonUtil;

    private final ChatClient chatClient;

    public HttpRequest prepareHttpRequest(String id) {

        log.info("Preparing HTTP request for expire payment with id: {}", id);

        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(stripeAPIKey, Constant.EMPTY_STRING);

        String expireSessionUrl = getExpireUrlTemplate.replace("{id}", id);
        log.info("Constructed expireSessionUrl: {}", expireSessionUrl);

        HttpRequest httpRequest = new HttpRequest();
        httpRequest.setHttpMethod(HttpMethod.POST);
        httpRequest.setUrl(expireSessionUrl);
        httpRequest.setHttpHeaders(headers);
        httpRequest.setRequestBody("");

        log.info("Prepared HTTP request: {}", httpRequest);

        return httpRequest;
    }

    public StripeResponse processResponse(ResponseEntity<String> httpResponse) {

        log.info("Processing HTTP response | httpResponse : {}", httpResponse);

        if (httpResponse.getStatusCode().is2xxSuccessful()) {
            log.info("HTTP response is successful");

            StripeResponse response = jsonUtil.convertJsonToObject(httpResponse.getBody(), StripeResponse.class);
            log.info("Parsed PaymentResponse: {}", response);

            if(response != null && response.getId() != null) {
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
                ErrorCodeEnum.GET_PAYMENT_FAILED.getErrorCode(),
                ErrorCodeEnum.GET_PAYMENT_FAILED.getErrorMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR
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