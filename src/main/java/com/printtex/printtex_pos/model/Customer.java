package com.printtex.printtex_pos.model;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Entity
@Table(name = "customer")
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "customer_id")
    private int id;

    @Column(name = "customer_name")
    @NotEmpty(message = "*Please provide customer name")
    private String customerName;

    @Column(name = "mobileNo")
    @NotEmpty(message = "*Please provide mobile no.")
    @Size(min = 14, max = 14, message = "Mobile number must be 14 digit")
    @Pattern(regexp = "^\\+8801[3456789][0-9]{8}\\b", message = "Mobile number must be start with +880")
    //@Pattern(regexp="^(?:\\+?88)?01[15-9]\\d{8}$", message = "Mobile number must be start with +880")
    private String mobileNo;

    @Column(name = "address")
    @NotEmpty(message = "*Please provide address.")
    private String address;
    private Long branchId;

    private double previousDueAmount;
    private int customerPreviousBillNo;

    public Customer() {
    }

    public Customer(String customerName, String mobileNo, String address, Long branchId, double previousDueAmount, int customerPreviousBillNo) {
        this.customerName = customerName;
        this.mobileNo = mobileNo;
        this.address = address;
        this.branchId = branchId;
        this.previousDueAmount = previousDueAmount;
        this.customerPreviousBillNo = customerPreviousBillNo;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Long getBranchId() {
        return branchId;
    }

    public void setBranchId(Long branchId) {
        this.branchId = branchId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getCustomerPreviousBillNo() {
        return customerPreviousBillNo;
    }

    public void setCustomerPreviousBillNo(int customerPreviousBillNo) {
        this.customerPreviousBillNo = customerPreviousBillNo;
    }

    public double getPreviousDueAmount() {
        return previousDueAmount;
    }

    public void setPreviousDueAmount(double previousDueAmount) {
        this.previousDueAmount = previousDueAmount;
    }
}
