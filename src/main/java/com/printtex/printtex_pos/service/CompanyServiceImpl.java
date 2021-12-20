package com.printtex.printtex_pos.service;

import com.printtex.printtex_pos.model.Company;
import com.printtex.printtex_pos.repository.CompanyRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CompanyServiceImpl implements CompanyService {
    private final CompanyRepository companyRepository;

    public CompanyServiceImpl(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

    @Override
    public void addCompany(Company company) {
        companyRepository.save(company);
    }

    @Override
    public List<Company> getCompanyList() {
        return companyRepository.findAll();
    }

    @Override
    public long countCompanyRowNo() {
        return companyRepository.count();
    }

    @Override
    public Company getCompanyByCompanyId(int companyId) {
        return companyRepository.findCompanyByCompanyId(companyId);
    }

}
