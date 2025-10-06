package com.hulkhiretech.payments.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class InvoiceGeneratorResponse {
    private String id;
    private String paymentStatus;
    private String hostedInvoiceUrl;
    private String invoicePdf;
    private String message;
}
