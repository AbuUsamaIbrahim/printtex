package com.printtex.printtex_pos.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.printtex.printtex_pos.controller.LoginController;
import com.printtex.printtex_pos.model.Customer;
import com.printtex.printtex_pos.model.User;
import com.printtex.printtex_pos.repository.CustomerRepository;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Service
public class CustomerServiceImpl implements CustomerService {
    final
    CustomerRepository customerRepository;
    private final EventLogService eventLogService;
    private final LoginController loginController;

    public CustomerServiceImpl(CustomerRepository customerRepository, EventLogService eventLogService, LoginController loginController) {
        this.customerRepository = customerRepository;
        this.eventLogService = eventLogService;
        this.loginController = loginController;
    }

    @Override
    public void addCustomer(Customer customer) {
        User user = loginController.getAuthenticatedUser();
        customer.setBranchId(user.getBranch().getId());
        customerRepository.save(customer);

    }

    @Override
    public List<Customer> getAllCustomer(Model model) {
        if (loginController.isBranch(model)) {
            User user = loginController.getAuthenticatedUser();
            return customerRepository.findAllByBranchIdOrderByCustomerNameAsc(user.getBranch().getId());
        }
        return customerRepository.findAllByOrderByCustomerName();
    }

    @Override
    public List<Customer> getAllCustomerByBranch(Long branchId) {
        return customerRepository.findAllByBranchIdOrderByCustomerNameAsc(branchId);
    }

    @Override
    public Customer getCustomerByCustomerId(int customerId) {
        return customerRepository.findCustomerById(customerId);
    }

    @Override
    public void deleteCustomer(int id, HttpServletRequest request) {
        Customer customer = customerRepository.findCustomerById(id);
        ObjectMapper mapper = new ObjectMapper();
        String oldJson = null;
        try {
            oldJson = mapper.writeValueAsString(customer);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        customerRepository.delete(customer);
        try {
            String userNewJson = mapper.writeValueAsString(customer);
            eventLogService.saveUpdateLog(userNewJson, oldJson, Customer.class, String.valueOf(customer.getId()), request);
        } catch (JsonProcessingException e) {

        }
    }

    @Override
    public List<Customer> getAllCustomerExceptUpdateCustomer(int id) {
        return customerRepository.findAllByWithoutUpdateCustomer(id);
    }

    @Override
    public List<Customer> getAllDueCustomers() {
        return customerRepository.getAllDueCustomers();
    }
}
