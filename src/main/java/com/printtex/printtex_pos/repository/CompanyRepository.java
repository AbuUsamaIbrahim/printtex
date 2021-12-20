package com.printtex.printtex_pos.repository;

import com.printtex.printtex_pos.model.Company;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompanyRepository extends JpaRepository<Company, Integer> {
    Company findCompanyByCompanyId(int companyId);

    int countByCompanyName(String companyName);
}
