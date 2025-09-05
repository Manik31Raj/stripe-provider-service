package com.hulkhiretech.payments.service.interfaces;

import com.hulkhiretech.payments.pojo.CreatePaymentRequest;
import com.hulkhiretech.payments.pojo.PaymentResponse;

public interface PaymentService {
    public PaymentResponse cratePayment(CreatePaymentRequest createPaymentRequest);

}
