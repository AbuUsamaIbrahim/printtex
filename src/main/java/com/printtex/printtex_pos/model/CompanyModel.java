package com.printtex.printtex_pos.model;


import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;


public class CompanyModel {
    @NotEmpty(message = "*please provide company Name")
    @Size(message = "*Company name must be contains at least 3 letters")
    private String companyName;
    private MultipartFile companyLogoUrl;
    private MultipartFile companyNameLogoUrl;
    @NotEmpty(message = "*Please Enter Company Address")
    private String companyAddress;


    public CompanyModel() {
    }

    public String getCompanyAddress() {
        return companyAddress;
    }

    public void setCompanyAddress(String companyAddress) {
        this.companyAddress = companyAddress;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public MultipartFile getCompanyLogoUrl() {
        return companyLogoUrl;
    }

    public void setCompanyLogoUrl(MultipartFile companyLogoUrl) {
        this.companyLogoUrl = companyLogoUrl;
    }

    public MultipartFile getCompanyNameLogoUrl() {
        return companyNameLogoUrl;
    }

    public void setCompanyNameLogoUrl(MultipartFile companyNameLogoUrl) {
        this.companyNameLogoUrl = companyNameLogoUrl;
    }
}
