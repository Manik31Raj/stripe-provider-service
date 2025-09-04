package com.hulkhiretech.payments.pojo;

import lombok.Data;

@Data
public class LineItem {
    private String currency;
    private int quantity;
    private String productName;
    private String productImageUrl;
    private String productDescription;
    private int unitAmount;

}
