package com.printtex.printtex_pos.dto;

import lombok.Data;


@Data
public class BillDto {
    private int billId;
    private String billNo;
    private String customerName;
    private String billingDate;
    private String paidAmount;
    private String previousDueAmount;
    private String newDueAmount;
    private String totalAmount;
    private String totalPayableAmount;
}
