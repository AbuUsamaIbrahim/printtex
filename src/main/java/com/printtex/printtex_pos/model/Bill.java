package com.printtex.printtex_pos.model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
public class Bill {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int billId;
    private String billNo;
    @OneToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;
    @OneToOne
    @JoinColumn(name = "salesperson_id")
    private Salesperson salesperson;

    private int totalQuantity;
    @OneToMany(cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    @JoinTable(name = "bill_sales", joinColumns = @JoinColumn(name = "bill_id"), inverseJoinColumns = @JoinColumn(name = "sale_id"))
    private List<Sale> sales = new ArrayList<>();
    @Temporal(TemporalType.DATE)
    private Date billingDate;
    private double paidAmount;
    private double previousDueAmount;
    private double newDueAmount;
    private double totalAmount;
    private double totalPayableAmount;
    private String paymentDate;
    @OneToOne
    @JoinColumn(name = "company_id")
    private Company company;
    private Long userId;
    private Long branchId;

    public Bill(Long userId, Long branchId, String billNo, Customer customer, Salesperson salesperson, int totalQuantity, List<Sale> sales, Date billingDate, double paidAmount, double previousDueAmount, double newDueAmount, double totalAmount, double totalPayableAmount, Company company) {
        this.userId = userId;
        this.branchId = branchId;
        this.billNo = billNo;
        this.customer = customer;
        this.salesperson = salesperson;
        this.totalQuantity = totalQuantity;
        this.sales = sales;
        this.billingDate = billingDate;
        this.paidAmount = paidAmount;
        this.previousDueAmount = previousDueAmount;
        this.newDueAmount = newDueAmount;
        this.totalAmount = totalAmount;
        this.totalPayableAmount = totalPayableAmount;
        this.company = company;
    }

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

    public Bill() {
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

    public Date getBillingDate() {
        return billingDate;
    }

    public void setBillingDate(Date billingDate) {
        this.billingDate = billingDate;
    }

    public double getPaidAmount() {
        return paidAmount;
    }

    public void setPaidAmount(double paidAmount) {
        this.paidAmount = paidAmount;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
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

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public double getTotalPayableAmount() {
        return totalPayableAmount;
    }

    public void setTotalPayableAmount(double totalPayableAmount) {
        this.totalPayableAmount = totalPayableAmount;
    }

    public double getPreviousDueAmount() {
        return previousDueAmount;
    }

    public void setPreviousDueAmount(double previousDueAmount) {
        this.previousDueAmount = previousDueAmount;
    }

    public double getNewDueAmount() {
        return newDueAmount;
    }

    public void setNewDueAmount(double newDueAmount) {
        this.newDueAmount = newDueAmount;
    }

    public List<Sale> getSales() {
        return sales;
    }

    public void setSales(List<Sale> sales) {
        this.sales = sales;
    }

    public String getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(String paymentDate) {
        this.paymentDate = paymentDate;
    }

    @Override
    public String toString() {
        return "Bill{" + "billId=" + billId + ", billNo=" + billNo + ", customer=" + customer + ", salesperson=" + salesperson + ", totalQuantity=" + totalQuantity + ", sales=" + sales + ", billingDate=" + billingDate + ", paidAmount=" + paidAmount + ", previousDueAmount=" + previousDueAmount + ", newDueAmount=" + newDueAmount + ", totalAmount=" + totalAmount + ", totalPayableAmount=" + totalPayableAmount + ", company=" + company + '}';
    }

}
