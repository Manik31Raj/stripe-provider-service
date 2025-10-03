package com.hulkhiretech.payments.util;

import com.hulkhiretech.payments.pojo.InvoiceGeneratorResponse;
import com.hulkhiretech.payments.stripe.StripeInvoiceGeneratorResponse;
import com.hulkhiretech.payments.stripe.StripeResponse;

public class StripeInvoiceResponseUtil {

    private StripeInvoiceResponseUtil() {
        // Private constructor to prevent instantiation
    }
    public static InvoiceGeneratorResponse getPaymentResponse(StripeInvoiceGeneratorResponse stripeInvoiceGeneratorResponse) {
        InvoiceGeneratorResponse response = new InvoiceGeneratorResponse();

        // Use the instance passed in
        response.setId(stripeInvoiceGeneratorResponse.getId());
        response.setPaymentStatus(stripeInvoiceGeneratorResponse.getPaymentStatus());
        if (stripeInvoiceGeneratorResponse.getInvoice() != null) {
            response.setHostedInvoiceUrl(stripeInvoiceGeneratorResponse.getInvoice().getHostedInvoiceUrl());
            response.setInvoicePdf(stripeInvoiceGeneratorResponse.getInvoice().getInvoicePdf());
        }
        if(response.getPaymentStatus().equals("paid")){
            response.setMessage("Invoice generated successfully. !! Visit Again");
        } else {
            response.setMessage("Complete the payment first to generate the invoice.");
        }

        return response;
    }
}

