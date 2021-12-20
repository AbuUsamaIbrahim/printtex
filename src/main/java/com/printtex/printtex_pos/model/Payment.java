package com.printtex.printtex_pos.model;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;

public class Payment {
    @Min(value = 0, message = "Paid amount can't be negative")
    private double paidPayment;
    private String paymentDate;

    public Payment(double paidPayment) {
        this.paidPayment = paidPayment;
    }

    public Payment() {
    }

    public String getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(String paymentDate) {
        this.paymentDate = paymentDate;
    }

    public double getPaidPayment() {
        return paidPayment;
    }

    public void setPaidPayment(double paidPayment) {
        this.paidPayment = paidPayment;
    }
}
