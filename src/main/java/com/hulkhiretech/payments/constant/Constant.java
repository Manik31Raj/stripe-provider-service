package com.hulkhiretech.payments.constant;

import com.hulkhiretech.payments.http.HttpServiceEngine;

public class Constant {

    private Constant(){
    }
    public static final String PAYMENTS = "/payments";
    public static final String EMPTY_STRING = "";
    public static final String MODE = "mode";
    public static final String MODE_PAYMENT = "payment";
    public static final String SUCCESS_URL = "success_url";
    public static final String CANCEL_URL = "cancel_url";

    public static final boolean INVOICE_CREATION_ENABLED = true;
}
