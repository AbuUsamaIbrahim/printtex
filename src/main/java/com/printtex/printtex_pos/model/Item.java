package com.printtex.printtex_pos.model;

import javax.persistence.*;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;


@Entity
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int itemId;
    @NotEmpty(message = "*please Enter Item Name")
    private String itemName;
    @Digits(integer = 20, fraction = 0, message = "*please Enter unit price of this item")
    private double itemUnitPrice;
    @Min(value = 0, message = "*Amount should be at least 0 kg")
    private int itemAmount;
    @OneToOne
    @JoinColumn(name = "category_id")
    private Category category;
    private Integer status;
    private Long branchId;

    public Item() {
    }

    public Item(Integer itemId, String itemName, double itemUnitPrice, int itemAmount, Category category, Integer status, Long branchId) {
        this.itemId = itemId;
        this.itemName = itemName;
        this.itemUnitPrice = itemUnitPrice;
        this.itemAmount = itemAmount;
        this.category = category;
        this.status = status;
        this.branchId = branchId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public Long getBranchId() {
        return branchId;
    }

    public void setBranchId(Long branchId) {
        this.branchId = branchId;
    }

    public Integer getItemId() {
        return itemId;
    }

    public void setItemId(Integer itemId) {
        this.itemId = itemId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public double getItemUnitPrice() {
        return itemUnitPrice;
    }

    public void setItemUnitPrice(double itemUnitPrice) {
        this.itemUnitPrice = itemUnitPrice;
    }

    public int getItemAmount() {
        return itemAmount;
    }

    public void setItemAmount(int itemAmount) {
        this.itemAmount = itemAmount;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

}
