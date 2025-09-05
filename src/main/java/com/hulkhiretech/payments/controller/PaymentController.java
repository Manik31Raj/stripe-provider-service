package com.hulkhiretech.payments.controller;

import com.hulkhiretech.payments.constant.Constant;
import com.hulkhiretech.payments.pojo.CreatePaymentRequest;
import com.hulkhiretech.payments.pojo.PaymentResponse;
import com.hulkhiretech.payments.service.interfaces.PaymentService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@Slf4j
@RequestMapping(Constant.PAYMENTS)
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    public PaymentResponse createPayment(@RequestBody CreatePaymentRequest createPaymentRequest) {
        {
            log.info("Creating a new payment | "
                    + "createPaymentRequest: {}", createPaymentRequest);
            PaymentResponse response = paymentService.cratePayment(createPaymentRequest);
            log.info("Payment creation response: {}", response);
            return response;
        }
    }
    @GetMapping("/{id}")
    public PaymentResponse getPayment(@PathVariable String id)
    {
        log.info("Get Payment API called id: {}",id);

        PaymentResponse response=paymentService.getPayment(id);
        log.info("Get Payment API response: {}",response);

        return response;
    }
}
