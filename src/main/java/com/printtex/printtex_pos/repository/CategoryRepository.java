package com.printtex.printtex_pos.repository;

import com.printtex.printtex_pos.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Integer> {
    Category findCategoryByCategoryId(int id);

    @Query("SELECT c FROM Category c WHERE NOT c.categoryId = :updateCategoryId")
    List<Category> findAllByWithoutUpdateCategory(@Param("updateCategoryId") int updateCategoryId);
}
