package com.hulkhiretech.payments.stripe;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class StripeInvoiceGeneratorResponse {
    private String id;

    @JsonProperty("payment_status")
    private String paymentStatus;

    private InvoiceDetails invoice;

    @Data
    public static class InvoiceDetails {
        @JsonProperty("hosted_invoice_url")
        private String hostedInvoiceUrl;

        @JsonProperty("invoice_pdf")
        private String invoicePdf;
    }
}
