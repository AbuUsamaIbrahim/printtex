package com.printtex.printtex_pos.repository;

import com.printtex.printtex_pos.model.Salesperson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SalespersonRepository extends JpaRepository<Salesperson, Integer> {
    Salesperson findSalespersonById(int id);

    @Query("SELECT c FROM Salesperson c WHERE NOT c.id = :updateSalespersonId")
    List<Salesperson> findAllByWithoutUpdateCustomer(@Param("updateSalespersonId") int updateSalespersonId);

    List<Salesperson> findAllByOrderBySalespersonName();

    List<Salesperson> findAllByBranchIdOrderBySalespersonName(Long branchId);
}
