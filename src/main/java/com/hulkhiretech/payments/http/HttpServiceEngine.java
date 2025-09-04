package com.hulkhiretech.payments.http;

import com.hulkhiretech.payments.constant.ErrorCodeEnum;
import com.hulkhiretech.payments.exception.StripeProviderException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClient;

@Slf4j
@Component
public class HttpServiceEngine {

    private RestClient restClient;

    public HttpServiceEngine(RestClient.Builder restClientbuilder) {
        this.restClient = restClientbuilder.build();
    }
     public  ResponseEntity<String> makeHttpCall(HttpRequest httpRequest) {
        log.info("Making an HTTP call ");
        try{
            ResponseEntity<String> httpResponse= restClient.method(HttpMethod.POST)
                    .uri(httpRequest.getUrl())
                    .headers(t-> t.addAll(httpRequest.getHttpHeaders()))
                    .body(httpRequest.getRequestBody())
                    .retrieve()
                    .toEntity(String.class);

            log.info("HTTP call completed with status code: {}", httpResponse);

            return httpResponse;

        }catch (HttpClientErrorException  | HttpServerErrorException e)
        {
            log.error("HTTP Client Error: {}", e.getMessage(),e);
             if (e.getStatusCode() == HttpStatus.GATEWAY_TIMEOUT || e.getStatusCode() == HttpStatus.SERVICE_UNAVAILABLE) {
                 throw new StripeProviderException(
                         ErrorCodeEnum.UNABLE_TO_CONNECT_TO_STRIPE.getErrorCode(),
                         ErrorCodeEnum.UNABLE_TO_CONNECT_TO_STRIPE.getErrorMessage(),
                         HttpStatus.SERVICE_UNAVAILABLE
                 );
             }
            return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsString());
        }
        catch (Exception e) {
            log.error("Error during HTTP call: {}", e.getMessage(),e);
            throw new StripeProviderException(
                    ErrorCodeEnum.UNABLE_TO_CONNECT_TO_STRIPE.getErrorCode(),
                    ErrorCodeEnum.UNABLE_TO_CONNECT_TO_STRIPE.getErrorMessage(),
                    HttpStatus.SERVICE_UNAVAILABLE
            );
        }
    }
}
