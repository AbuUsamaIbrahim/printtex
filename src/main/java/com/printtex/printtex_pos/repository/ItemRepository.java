package com.printtex.printtex_pos.repository;

import com.printtex.printtex_pos.model.Category;
import com.printtex.printtex_pos.model.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Integer> {
    Item findItemByItemId(int id);

    @Query("SELECT i FROM Item i WHERE i.status = 1 AND NOT i.itemId = :updateItemId")
    List<Item> findAllByWithoutUpdateItem(@Param("updateItemId") int updateItemId);

    @Query(value = "select i from Item i where i.status = 1 AND i.category.categoryId = :categoryId")
    List<Item> getAllItemByCategoryId(@Param("categoryId") int categoryId);

    List<Item> findAllByStatusAndBranchIdAndCategory(Integer status, Long branchId, Category category);

    List<Item> findAllByStatusAndBranchIdAndCategoryOrderByItemNameAsc(Integer status, Long branchId, Category category);

    List<Item> findAllByStatusAndBranchIdOrderByItemNameAsc(Integer status, Long Branch);

    List<Item> findAllByStatusOrderByItemNameAsc(Integer status);

    @Query(value = "select i from Item i where i.status = 1 AND i.branchId = :branchId")
    List<Item> getAllItemByBranchId(@Param("branchId") Long branchId);

    int countByItemNameAndBranchIdAndStatus(String itemName, Long branchId, Integer status);

}
