package com.hulkhiretech.payments.constant;

import lombok.Getter;

@Getter
public enum ErrorCodeEnum {
    GENERIC_ERROR("40000", "Internal Server Error"),
    INVAlID_QUANTITY("40001", "Invalid Quantity : Provide quantity with 1 or above"),
    UNABLE_TO_CONNECT_TO_STRIPE("40002", "Unable to connect to stripe"),
    GET_PAYMENT_FAILED("40003", "Payment creation failed"),
    STRIPE_ERROR("40004", "<dynamically prepare from stripe error response>"),
    INVALID_STRIPE_SIGNATURE("40005", "Invalid Stripe Signature"),
    PAYMENT_INCOMPLETE("40006", "Payment is not completed !! Kindly complete payment to generate invoice");

    private final String errorCode;
    private final String errorMessage;

    ErrorCodeEnum(String errorCode, String errorMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }
}
