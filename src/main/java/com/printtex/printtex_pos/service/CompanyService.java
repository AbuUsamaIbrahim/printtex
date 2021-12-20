package com.printtex.printtex_pos.service;

import com.printtex.printtex_pos.model.Company;

import java.util.List;

public interface CompanyService {
    void addCompany(Company company);

    List<Company> getCompanyList();

    long countCompanyRowNo();

    Company getCompanyByCompanyId(int companyId);
}
