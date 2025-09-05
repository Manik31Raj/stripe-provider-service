package com.hulkhiretech.payments.stripe;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class StripeResponse {
    private String id;
    private String url;

    @JsonProperty("status")
    private String sessionStatus;

    @JsonProperty("payment_status")
    private String paymentStatus;
}
