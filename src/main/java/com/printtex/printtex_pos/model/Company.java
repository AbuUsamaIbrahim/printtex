package com.printtex.printtex_pos.model;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;

@Entity
public class Company {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int companyId;
    private String companyName;
    private String companyLogoUrl;
    private String companyNameLogoUrl;
    @NotEmpty(message = "*Please give company address")
    private String companyAddress;

    public Company() {
    }

    public Company(Integer companyId, String companyName, String companyLogoUrl, String companyNameLogoUrl, String companyAddress) {
        this.companyId = companyId;
        this.companyName = companyName;
        this.companyLogoUrl = companyLogoUrl;
        this.companyNameLogoUrl = companyNameLogoUrl;
        this.companyAddress = companyAddress;
    }

    public String getCompanyAddress() {
        return companyAddress;
    }

    public void setCompanyAddress(String companyAddress) {
        this.companyAddress = companyAddress;
    }

    public int getCompanyId() {
        return companyId;
    }

    public void setCompanyId(int companyId) {
        this.companyId = companyId;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getCompanyLogoUrl() {
        return companyLogoUrl;
    }

    public void setCompanyLogoUrl(String companyLogoUrl) {
        this.companyLogoUrl = companyLogoUrl;
    }

    public String getCompanyNameLogoUrl() {
        return companyNameLogoUrl;
    }

    public void setCompanyNameLogoUrl(String companyNameLogoUrl) {
        this.companyNameLogoUrl = companyNameLogoUrl;
    }
}
