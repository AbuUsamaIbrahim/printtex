package com.printtex.printtex_pos.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Zahangir Alam on 2018-07-11.
 */
public class BillDTO {
    private int billId;
    private String billNo;
    private Customer customer;
    private Salesperson salesperson;
    private int totalQuantity;
    private List<Sale> sales = new ArrayList<>();
    private Date billingDate;
    private String paidAmount;
    private String previousDueAmount;
    private String newDueAmount;
    private String totalAmount;
    private String totalPayableAmount;
    private Company company;
    private Long userId;
    private Long branchId;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getBranchId() {
        return branchId;
    }

    public void setBranchId(Long branchId) {
        this.branchId = branchId;
    }

    public int getBillId() {
        return billId;
    }

    public void setBillId(int billId) {
        this.billId = billId;
    }

    public String getBillNo() {
        return billNo;
    }

    public void setBillNo(String billNo) {
        this.billNo = billNo;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Salesperson getSalesperson() {
        return salesperson;
    }

    public void setSalesperson(Salesperson salesperson) {
        this.salesperson = salesperson;
    }

    public int getTotalQuantity() {
        return totalQuantity;
    }

    public void setTotalQuantity(int totalQuantity) {
        this.totalQuantity = totalQuantity;
    }

    public List<Sale> getSales() {
        return sales;
    }

    public void setSales(List<Sale> sales) {
        this.sales = sales;
    }

    public Date getBillingDate() {
        return billingDate;
    }

    public void setBillingDate(Date billingDate) {
        this.billingDate = billingDate;
    }

    public String getPaidAmount() {
        return paidAmount;
    }

    public void setPaidAmount(String paidAmount) {
        this.paidAmount = paidAmount;
    }

    public String getPreviousDueAmount() {
        return previousDueAmount;
    }

    public void setPreviousDueAmount(String previousDueAmount) {
        this.previousDueAmount = previousDueAmount;
    }

    public String getNewDueAmount() {
        return newDueAmount;
    }

    public void setNewDueAmount(String newDueAmount) {
        this.newDueAmount = newDueAmount;
    }

    public String getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(String totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getTotalPayableAmount() {
        return totalPayableAmount;
    }

    public void setTotalPayableAmount(String totalPayableAmount) {
        this.totalPayableAmount = totalPayableAmount;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }
}
