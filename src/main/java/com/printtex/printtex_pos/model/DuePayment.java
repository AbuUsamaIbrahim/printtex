package com.printtex.printtex_pos.model;

public class DuePayment {
    private Customer customer;
    private double paidDueAmount;

    public DuePayment() {
    }

    public DuePayment(Customer customer, double paidDueAmount) {
        this.customer = customer;
        this.paidDueAmount = paidDueAmount;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public double getPaidDueAmount() {
        return paidDueAmount;
    }

    public void setPaidDueAmount(double paidDueAmount) {
        this.paidDueAmount = paidDueAmount;
    }
}
