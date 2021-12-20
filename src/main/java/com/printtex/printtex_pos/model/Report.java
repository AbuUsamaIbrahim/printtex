package com.printtex.printtex_pos.model;

public class Report {
    private String startingDate;
    private String endingDate;
    private Customer customer;
    private Salesperson salesperson;
    private Item item;
    private Branch branch;
    private Bill bill;


    public Report() {
    }

    public Report(Bill bill, Branch branch, String startingDate, String endingDate, Customer customer, Salesperson salesperson, Item item) {
        this.bill = bill;
        this.branch = branch;
        this.startingDate = startingDate;
        this.endingDate = endingDate;
        this.customer = customer;
        this.salesperson = salesperson;
        this.item = item;
    }

    public Bill getBill() {
        return bill;
    }

    public void setBill(Bill bill) {
        this.bill = bill;
    }

    public Branch getBranch() {
        return branch;
    }

    public void setBranch(Branch branch) {
        this.branch = branch;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public String getStartingDate() {
        return startingDate;
    }

    public void setStartingDate(String startingDate) {
        this.startingDate = startingDate;
    }

    public String getEndingDate() {
        return endingDate;
    }

    public void setEndingDate(String endingDate) {
        this.endingDate = endingDate;
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
}
