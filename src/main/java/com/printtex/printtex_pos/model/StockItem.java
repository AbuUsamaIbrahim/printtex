package com.printtex.printtex_pos.model;

public class StockItem {
    private int stockItemId;
    private String stockItemName;
    private int stockRemainingItem;
    private int stockSaleItem;
    private String stockCategoryName;
    private int totalItem;

    public StockItem(int stockItemId, String stockItemName, int stockRemainingItem, int stockSaleItem, String stockCategoryName, int totalItem) {
        this.stockItemId = stockItemId;
        this.stockItemName = stockItemName;
        this.stockRemainingItem = stockRemainingItem;
        this.stockSaleItem = stockSaleItem;
        this.stockCategoryName = stockCategoryName;
        this.totalItem = totalItem;
    }

    public StockItem() {
    }

    public int getStockItemId() {
        return stockItemId;
    }

    public void setStockItemId(int stockItemId) {
        this.stockItemId = stockItemId;
    }

    public String getStockItemName() {
        return stockItemName;
    }

    public void setStockItemName(String stockItemName) {
        this.stockItemName = stockItemName;
    }

    public int getStockRemainingItem() {
        return stockRemainingItem;
    }

    public void setStockRemainingItem(int stockRemainingItem) {
        this.stockRemainingItem = stockRemainingItem;
    }

    public int getStockSaleItem() {
        return stockSaleItem;
    }

    public void setStockSaleItem(int stockSaleItem) {
        this.stockSaleItem = stockSaleItem;
    }

    public String getStockCategoryName() {
        return stockCategoryName;
    }

    public void setStockCategoryName(String stockCategoryName) {
        this.stockCategoryName = stockCategoryName;
    }

    public int getTotalItem() {
        return totalItem;
    }

    public void setTotalItem(int totalItem) {
        this.totalItem = totalItem;
    }
}
