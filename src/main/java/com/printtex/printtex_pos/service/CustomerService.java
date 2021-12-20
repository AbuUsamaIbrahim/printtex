package com.printtex.printtex_pos.service;


import com.printtex.printtex_pos.model.Customer;
import org.springframework.ui.Model;


import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface CustomerService {
    void addCustomer(Customer customer);

    List<Customer> getAllCustomer(Model model);

    List<Customer> getAllCustomerByBranch(Long branchId);

    Customer getCustomerByCustomerId(int customerId);

    void deleteCustomer(int id, HttpServletRequest request);

    List<Customer> getAllCustomerExceptUpdateCustomer(int id);

    List<Customer> getAllDueCustomers();

}
