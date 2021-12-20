package com.printtex.printtex_pos.model;

/**
 * Created by Zahangir Alam on 2018-07-09.
 */
public class CustomerDTO {
    private Integer id;
    private String customerName;
    private String mobileNo;
    private String address;
    private String previousDueAmount;
    private int customerPreviousBillNo;


    public CustomerDTO() {
    }

    public CustomerDTO(Integer id, String customerName, String mobileNo, String address, String previousDueAmount, int customerPreviousBillNo) {
        this.id = id;
        this.customerName = customerName;
        this.mobileNo = mobileNo;
        this.address = address;
        this.previousDueAmount = previousDueAmount;
        this.customerPreviousBillNo = customerPreviousBillNo;
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

    public String getPreviousDueAmount() {
        return previousDueAmount;
    }

    public void setPreviousDueAmount(String previousDueAmount) {
        this.previousDueAmount = previousDueAmount;
    }

    public int getCustomerPreviousBillNo() {
        return customerPreviousBillNo;
    }

    public void setCustomerPreviousBillNo(int customerPreviousBillNo) {
        this.customerPreviousBillNo = customerPreviousBillNo;
    }
}
