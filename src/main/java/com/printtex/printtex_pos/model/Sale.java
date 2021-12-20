package com.printtex.printtex_pos.model;


import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import java.util.Date;

@Entity
public class Sale {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int saleId;
    @Temporal(TemporalType.DATE)
    private Date saleDate;
    @OneToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;
    @OneToOne
    @JoinColumn(name = "salesperson_id")
    private Salesperson salesperson;
    @OneToOne
    @JoinColumn(name = "item_id")
    private Item item;
    @OneToOne
    @JoinColumn(name = "company_id")
    private Company company;
    @Digits(integer = 20, fraction = 0, message = "*please Enter unit price of this item")
    private double saleUnitPrice;
    @Min(value = 1, message = "*Quantity should be at least 1 kg")
    private int saleAmount;
    @Min(value = 1, message = "*Drum/Cartoon should be at least 1")
    private int saleDrumNo;
    @NotEmpty(message = "*Please select Drum/Cartoon")
    private String drumOrCartoon;
    private double totalSaleAmount;
    private String saleStatus;
    @OneToOne
    @JoinColumn(name = "bill_id")
    @JsonIgnore
    private Bill bill;
    private Long userId;
    private Long branchId;

    public Sale() {
    }

    public Sale(Long userId, Long branchId, Date saleDate, User user, Customer customer, Salesperson salesperson, Item item, Company company, double saleUnitPrice, int saleAmount, int saleDrumNo, String drumOrCartoon, double totalSaleAmount, String saleStatus, Bill bill) {
        this.saleDate = saleDate;
        this.userId = userId;
        this.branchId = branchId;
        this.customer = customer;
        this.salesperson = salesperson;
        this.item = item;
        this.company = company;
        this.saleUnitPrice = saleUnitPrice;
        this.saleAmount = saleAmount;
        this.saleDrumNo = saleDrumNo;
        this.drumOrCartoon = drumOrCartoon;
        this.totalSaleAmount = totalSaleAmount;
        this.saleStatus = saleStatus;
        this.bill = bill;
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

    public int getSaleId() {
        return saleId;
    }

    public void setSaleId(int saleId) {
        this.saleId = saleId;
    }

    public Date getSaleDate() {
        return saleDate;
    }

    public void setSaleDate(Date saleDate) {
        this.saleDate = saleDate;
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

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public double getSaleUnitPrice() {
        return saleUnitPrice;
    }

    public void setSaleUnitPrice(double saleUnitPrice) {
        this.saleUnitPrice = saleUnitPrice;
    }

    public int getSaleAmount() {
        return saleAmount;
    }

    public void setSaleAmount(int saleAmount) {
        this.saleAmount = saleAmount;
    }

    public int getSaleDrumNo() {
        return saleDrumNo;
    }

    public void setSaleDrumNo(int saleDrumNo) {
        this.saleDrumNo = saleDrumNo;
    }

    public String getDrumOrCartoon() {
        return drumOrCartoon;
    }

    public void setDrumOrCartoon(String drumOrCartoon) {
        this.drumOrCartoon = drumOrCartoon;
    }

    public double getTotalSaleAmount() {
        return totalSaleAmount;
    }

    public void setTotalSaleAmount(double totalSaleAmount) {
        this.totalSaleAmount = totalSaleAmount;
    }

    public String getSaleStatus() {
        return saleStatus;
    }

    public void setSaleStatus(String saleStatus) {
        this.saleStatus = saleStatus;
    }

    public Bill getBill() {
        return bill;
    }

    public void setBill(Bill bill) {
        this.bill = bill;
    }
}