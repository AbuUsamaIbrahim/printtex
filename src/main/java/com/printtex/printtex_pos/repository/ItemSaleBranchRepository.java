package com.printtex.printtex_pos.repository;

import com.printtex.printtex_pos.model.ItemSaleBranch;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItemSaleBranchRepository extends JpaRepository<ItemSaleBranch, Long> {
    void deleteAllBySaleId(Integer saleId);

    List<ItemSaleBranch> findAllByBranchIdOrderByItemIdAsc(Long branchId);

    List<ItemSaleBranch> findAllByBranchIdAndItemId(Long branchId, Integer itemId);
}
