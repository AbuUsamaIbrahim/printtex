package com.printtex.printtex_pos.dto;

import lombok.Data;

import java.util.List;

@Data
public class BillChalanDto {
    private String companyLogo;
    private String companyLogoName;
    private String companyAddress;
    private String branchName;
    private String companyName;
    private String footerBranchAddress;
    private String customerName;
    private String customerAddress;
    private String customerPhone;
    private String collectionDate;
    private String billNo;
    private String previousDue;
    private String currentBill;
    private String totalPayable;
    private String currentBillCollection;
    private String totalDue;
    private String previousCollectionDate;
    private String totalQuantity;
    private String totalInWord;
    private List<SaleDto> saleList;
}
