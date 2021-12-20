package com.printtex.printtex_pos.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.printtex.printtex_pos.model.Category;
import com.printtex.printtex_pos.repository.CategoryRepository;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {
    final
    CategoryRepository categoryRepository;
    private final EventLogService eventLogService;

    public CategoryServiceImpl(CategoryRepository categoryRepository, EventLogService eventLogService) {
        this.categoryRepository = categoryRepository;
        this.eventLogService = eventLogService;
    }

    @Override
    public void addCategory(Category category) {
        categoryRepository.save(category);
    }

    @Override
    public List<Category> getAllCategory() {
        return categoryRepository.findAll();
    }

    @Override
    public Category findCategoryByCategoryId(int id) {
        return categoryRepository.findCategoryByCategoryId(id);
    }

    @Override
    public void deleteCategory(int categoryId, HttpServletRequest request) {
        Category category = categoryRepository.findCategoryByCategoryId(categoryId);
        ObjectMapper mapper = new ObjectMapper();
        String oldJson = null;
        try {
            oldJson = mapper.writeValueAsString(category);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        categoryRepository.delete(category);

        eventLogService.saveDeleteLog(oldJson, Category.class, String.valueOf(category.getCategoryId()), request);
    }

    @Override
    public List<Category> getAllCategoryExceptUpdateCategory(int categoryId) {
        return categoryRepository.findAllByWithoutUpdateCategory(categoryId);
    }
}
