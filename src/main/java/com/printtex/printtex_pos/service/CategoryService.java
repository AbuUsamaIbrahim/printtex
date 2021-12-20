package com.printtex.printtex_pos.service;

import com.printtex.printtex_pos.model.Category;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface CategoryService {
    void addCategory(Category category);

    List<Category> getAllCategory();

    Category findCategoryByCategoryId(int id);

    void deleteCategory(int categoryId, HttpServletRequest request);

    List<Category> getAllCategoryExceptUpdateCategory(int categoryId);

}
