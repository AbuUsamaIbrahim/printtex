package com.printtex.printtex_pos.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.printtex.printtex_pos.model.Category;
import com.printtex.printtex_pos.service.CategoryService;
import com.printtex.printtex_pos.service.EventLogService;
import com.printtex.printtex_pos.service.ModelMessageService;
import org.springframework.http.HttpRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@Controller
public class CategoryController {
    private final CategoryService categoryService;
    private final ModelMessageService modelMessageService;
    private final EventLogService eventLogService;

    public CategoryController(CategoryService categoryService, ModelMessageService modelMessageService, EventLogService eventLogService) {
        this.categoryService = categoryService;
        this.modelMessageService = modelMessageService;
        this.eventLogService = eventLogService;
    }

    @GetMapping(value = "/admin/addcategory")
    public String createCategory(Model model) {
        modelMessageService.setModelMessage(model, "category", new Category());
        modelMessageService.setEmptyResultMessage(model);
        return "addcategory";
    }

    @PostMapping("/admin/addcategory")
    public String addCategory(@Valid @ModelAttribute Category category, BindingResult bindingResult, Model model, HttpServletRequest request) {
        List<Category> categories = categoryService.getAllCategory();
        return addOrUpdateCategory(categories, category, bindingResult, model, "addcategory", request);
    }

    @GetMapping(value = {"/admin/allcategory", "/branch/allcategory"})
    public String getCategoryList(Model model) {
        List<Category> categories = categoryService.getAllCategory();
        if (!categories.isEmpty()) {
            modelMessageService.setEmptyResultMessage(model);
            modelMessageService.setModelMessage(model, "categories", categories);
        } else {
            modelMessageService.setModelMessage(model, "result", "No Category Yet added");
        }
        return "listcategory";
    }

    @GetMapping("/admin/category/{action}/id/{categoryId}")
    public String deleteOrEditCategory(@PathVariable("categoryId") String categoryId, @PathVariable("action") String action, Model model, HttpServletRequest request) {
        List<Category> categories = categoryService.getAllCategory();

        if (categories.isEmpty()) {
            modelMessageService.setModelMessage(model, "result", "No Category Yet added");
            return "listcategory";
        }
        modelMessageService.setModelMessage(model, "categories", getCategories());
        if (action.equals("delete")) {
            categoryService.deleteCategory(Integer.valueOf(categoryId), request);
            return "redirect:/admin/allcategory";
        }
        if (action.equals("edit")) {
            modelMessageService.setModelMessage(model, "result", "Update Category");
            modelMessageService.setModelMessage(model, "category", categoryService.findCategoryByCategoryId(Integer.valueOf(categoryId)));
            return "updatecategory";
        }
        modelMessageService.setModelMessage(model, "result", "No Category selected to Update or delete");
        return "listcategory";
    }

    @PostMapping("/admin/category/update")
    public String updateCategory(@Valid @ModelAttribute Category category, BindingResult bindingResult, Model model, HttpServletRequest request) {
        List<Category> categories = categoryService.getAllCategoryExceptUpdateCategory(category.getCategoryId());
        return addOrUpdateCategory(categories, category, bindingResult, model, "listcategory", request);
    }

    @GetMapping("/admin/category/update")
    public String showAllCategories() {
        return "redirect:/admin/allcategory";
    }

    private String addOrUpdateCategory(List<Category> categoryList, Category category, BindingResult bindingResult, Model model, String returnString, HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            modelMessageService.setModelMessage(model, "result", "Category can't be saved with wrong input");
            return returnString;
        }
        ObjectMapper mapper = new ObjectMapper();
        String oldJson = null;
        try {
            oldJson = mapper.writeValueAsString(category);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        boolean[] existCategoryName = {false};
        categoryList.forEach(existingCategory -> {
            if (existingCategory.getCategoryName().equalsIgnoreCase(category.getCategoryName())) {
                existCategoryName[0] = true;
            }
        });
        if (existCategoryName[0]) {
            model.addAttribute("result", "This category name is already used ");
            return returnString;
        }
        categoryService.addCategory(category);
        try {
            String userNewJson = mapper.writeValueAsString(category);
            eventLogService.saveUpdateLog(userNewJson, oldJson, Category.class, String.valueOf(category.getCategoryId()), request);
            eventLogService.saveInsertLog(userNewJson, Category.class, String.valueOf(category.getCategoryId()), request);
        } catch (JsonProcessingException e) {

        }
        if (returnString.equals("listcategory")) {
            try {
                String userNewJson = mapper.writeValueAsString(category);
                eventLogService.saveUpdateLog(userNewJson, oldJson, Category.class, String.valueOf(category.getCategoryId()), request);
//                eventLogService.saveInsertLog(userNewJson, Category.class, String.valueOf(category.getCategoryId()), request);
            } catch (JsonProcessingException e) {

            }
            modelMessageService.setModelMessage(model, "categories", getCategories());
            modelMessageService.setModelMessage(model, "result", "Category updated successfully");
        }
        if (returnString.equals("addcategory")) {
            modelMessageService.setModelMessage(model, "result", "Category added successfully");
        }

        return returnString;
    }

    private List<Category> getCategories() {
        return categoryService.getAllCategory();
    }
}
