package com.hulkhiretech.payments.pojo;

import lombok.Data;

import java.util.List;
@Data
public class CreatePaymentRequest {
    private String successUrl;
    private String cancelUrl;
    private List<LineItem> lineItems;
}
