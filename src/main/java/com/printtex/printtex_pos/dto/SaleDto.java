package com.printtex.printtex_pos.dto;

import lombok.Data;

@Data
public class SaleDto {
    private String goods;
    private String quantity;
    private String quantityChallan;
    private String unitPrice;
    private String totalPrice;
}
