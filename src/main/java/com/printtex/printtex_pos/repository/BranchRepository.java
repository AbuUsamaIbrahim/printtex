package com.printtex.printtex_pos.repository;

import com.printtex.printtex_pos.model.Branch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BranchRepository extends JpaRepository<Branch, Long> {
    List<Branch> findAllById(Long id);

    Branch findBranchById(long id);

    @Query("SELECT b FROM Branch b WHERE NOT b.id = :updateBranchId")
    List<Branch> findAllByWithoutUpdateCustomer(@Param("updateBranchId") long id);

    List<Branch> findAllByOrderByNameAsc();

    int countByName(String name);

    List<Branch> findAllByIsMainBranchTrue();

    List<Branch> findAllByIsMainBranchFalse();
}
