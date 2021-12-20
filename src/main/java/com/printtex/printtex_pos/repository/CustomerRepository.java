package com.printtex.printtex_pos.repository;

import com.printtex.printtex_pos.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CustomerRepository extends JpaRepository<Customer, Integer> {
    Customer findCustomerById(int customerId);

    @Query("SELECT c FROM Customer c WHERE NOT c.id = :updateCustomerId")
    List<Customer> findAllByWithoutUpdateCustomer(@Param("updateCustomerId") int updateCustomerId);

    @Query("SELECT c FROM Customer c WHERE NOT c.previousDueAmount = 0")
    List<Customer> getAllDueCustomers();

    List<Customer> findAllByOrderByCustomerName();

    List<Customer> findAllByBranchIdOrderByCustomerNameAsc(Long branchId);
}
