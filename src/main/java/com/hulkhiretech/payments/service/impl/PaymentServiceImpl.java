package com.hulkhiretech.payments.service.impl;

import com.hulkhiretech.payments.constant.ErrorCodeEnum;
import com.hulkhiretech.payments.exception.StripeProviderException;
import com.hulkhiretech.payments.http.HttpRequest;
import com.hulkhiretech.payments.http.HttpServiceEngine;
import com.hulkhiretech.payments.pojo.CreatePaymentRequest;
import com.hulkhiretech.payments.pojo.PaymentResponse;
import com.hulkhiretech.payments.service.helper.CreatePaymentHelper;
import com.hulkhiretech.payments.service.helper.ExpirePaymentHelper;
import com.hulkhiretech.payments.service.helper.GetPaymentHelper;
import com.hulkhiretech.payments.service.interfaces.PaymentService;
import com.hulkhiretech.payments.stripe.StripeResponse;
import com.hulkhiretech.payments.util.StripeResponseUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import static com.hulkhiretech.payments.util.StripeResponseUtil.getPaymentResponse;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final HttpServiceEngine httpServiceEngine;

    private final CreatePaymentHelper createPaymentHelper;

    private final GetPaymentHelper getPaymentHelper;

    private final ExpirePaymentHelper expirePaymentHelper;

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

        StripeResponse stripeResponse = createPaymentHelper.processResponse(httpResponse);
        log.info("Final Payment creation to be returned : {}", stripeResponse);

        PaymentResponse response = StripeResponseUtil.getPaymentResponse(stripeResponse);
        log.info("PaymentResponse to be returned : {}", response);

        return response;
    }
    @Override
    public PaymentResponse getPayment(String id) {
        log.info("Get Payment API called id: {}",id);

        HttpRequest httpRequest = getPaymentHelper.prepareHttpRequest(id);

        ResponseEntity<String> httpResponse=httpServiceEngine.makeHttpCall(httpRequest);
        log.info("HTTP call response: {}", httpResponse);

        StripeResponse stripeResponse = getPaymentHelper.processResponse(httpResponse);
        log.info("Final Payment creation to be returned : {}", stripeResponse);

        PaymentResponse response = StripeResponseUtil.getPaymentResponse(stripeResponse);
        log.info("PaymentResponse to be returned : {}", response);

        return response;
    }

    @Override
    public PaymentResponse expirePayment(String id) {
        log.info("Expire Payment API called id: {}",id);

        HttpRequest httpRequest = expirePaymentHelper.prepareHttpRequest(id);

        ResponseEntity<String> httpResponse=httpServiceEngine.makeHttpCall(httpRequest);
        log.info("HTTP call response: {}", httpResponse);

        StripeResponse stripeResponse = expirePaymentHelper.processResponse(httpResponse);
        log.info("Final Payment creation to be returned : {}", stripeResponse);

        PaymentResponse response = StripeResponseUtil.getPaymentResponse(stripeResponse);
        log.info("PaymentResponse to be returned : {}", response);

        return response;
    }
}
