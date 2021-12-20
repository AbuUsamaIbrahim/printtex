package com.printtex.printtex_pos.service;

import com.printtex.printtex_pos.model.Salesperson;
import org.springframework.ui.Model;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface SalespersonService {
    void addSalesperson(Salesperson salesperson, HttpServletRequest request);

    List<Salesperson> getAll();

    void deleteSalesperson(int id, HttpServletRequest request);

    Salesperson findSalespersonById(int id);

    List<Salesperson> getAllSalespersonExceptUpdateSalesperson(int id);

    List<Salesperson> getAllSalesPersonByBranch(Model model);

    List<Salesperson> getAllSalesPersonByBranchId(Long branchId);
}
