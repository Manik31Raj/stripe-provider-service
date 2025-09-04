package com.hulkhiretech.payments.exception;

import lombok.Getter;
import lombok.ToString;
import org.springframework.http.HttpStatus;

import java.io.Serial;

@Getter
@ToString
public class StripeProviderException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1000000;

    private final String errorCode;
    private final String errorMessage;
    private final HttpStatus httpStatus;

    public StripeProviderException(String errorCode, String errorMessage, HttpStatus httpStatus) {
        super(errorMessage);
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.httpStatus = httpStatus;
    }
}