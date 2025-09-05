package com.hulkhiretech.payments.util;

import com.hulkhiretech.payments.pojo.PaymentResponse;
import com.hulkhiretech.payments.stripe.StripeResponse;

public class StripeResponseUtil {

    private StripeResponseUtil() {
        // Private constructor to prevent instantiation
    }
    public static PaymentResponse getPaymentResponse(StripeResponse stripeResponse) {
        PaymentResponse response=new PaymentResponse();
        response.setId(stripeResponse.getId());
        response.setUrl(stripeResponse.getUrl());
        response.setSessionStatus(stripeResponse.getSessionStatus());
        response.setPaymentStatus(stripeResponse.getPaymentStatus());
        return response;
    }
}
