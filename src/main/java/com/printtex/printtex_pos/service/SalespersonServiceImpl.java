package com.printtex.printtex_pos.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.printtex.printtex_pos.controller.LoginController;
import com.printtex.printtex_pos.model.Salesperson;
import com.printtex.printtex_pos.model.User;
import com.printtex.printtex_pos.repository.SalespersonRepository;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Service
public class SalespersonServiceImpl implements SalespersonService {
    private final SalespersonRepository salespersonRepository;
    private final EventLogService eventLogService;
    private final LoginController loginController;

    public SalespersonServiceImpl(SalespersonRepository salespersonRepository, EventLogService eventLogService, LoginController loginController) {
        this.salespersonRepository = salespersonRepository;
        this.eventLogService = eventLogService;
        this.loginController = loginController;
    }

    @Override
    public void addSalesperson(Salesperson salesperson, HttpServletRequest request) {
        User user = loginController.getAuthenticatedUser();
        salesperson.setBranchId(user.getBranch().getId());
        salespersonRepository.save(salesperson);
    }

    @Override
    public List<Salesperson> getAll() {
        User user = loginController.getAuthenticatedUser();
        if (user.getRole().equals("1")) {
            return salespersonRepository.findAllByOrderBySalespersonName();
        }
        return salespersonRepository.findAllByBranchIdOrderBySalespersonName(user.getBranch().getId());

    }

    @Override
    public void deleteSalesperson(int id, HttpServletRequest request) {
        Salesperson salesperson = salespersonRepository.findSalespersonById(id);
        ObjectMapper mapper = new ObjectMapper();
        String oldJson = null;
        try {
            oldJson = mapper.writeValueAsString(salesperson);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        salespersonRepository.delete(salesperson);
        eventLogService.saveDeleteLog(oldJson, Salesperson.class, String.valueOf(salesperson.getId()), request);
    }

    @Override
    public Salesperson findSalespersonById(int id) {
        return salespersonRepository.findSalespersonById(id);
    }

    @Override
    public List<Salesperson> getAllSalespersonExceptUpdateSalesperson(int id) {
        return salespersonRepository.findAllByWithoutUpdateCustomer(id);

    }

    @Override
    public List<Salesperson> getAllSalesPersonByBranch(Model model) {
        User user = loginController.getAuthenticatedUser();
        return salespersonRepository.findAllByBranchIdOrderBySalespersonName(user.getBranch().getId());
    }

    @Override
    public List<Salesperson> getAllSalesPersonByBranchId(Long branchId) {
        return salespersonRepository.findAllByBranchIdOrderBySalespersonName(branchId);
    }

}
