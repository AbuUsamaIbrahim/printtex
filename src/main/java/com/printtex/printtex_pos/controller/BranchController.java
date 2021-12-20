package com.printtex.printtex_pos.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.printtex.printtex_pos.model.*;
import com.printtex.printtex_pos.repository.BranchRepository;
import com.printtex.printtex_pos.repository.CustomerRepository;
import com.printtex.printtex_pos.repository.SalespersonRepository;
import com.printtex.printtex_pos.service.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@Controller
public class BranchController {
    private final BranchRepository branchRepository;
    private final BranchService branchService;
    private final ModelMessageService modelMessageService;
    private final EventLogService eventLogService;
    private final CustomerRepository customerRepository;
    private final ItemService itemService;
    private final SalespersonRepository salespersonRepository;

    public BranchController(BranchRepository branchRepository, ModelMessageService modelMessageService, BranchService branchService, EventLogService eventLogService, CustomerRepository customerRepository, ItemService itemService, SalespersonRepository salespersonRepository) {
        this.branchRepository = branchRepository;
        this.modelMessageService = modelMessageService;
        this.branchService = branchService;
        this.eventLogService = eventLogService;
        this.customerRepository = customerRepository;
        this.itemService = itemService;
        this.salespersonRepository = salespersonRepository;

    }

    @GetMapping(value = "/admin/addBranch")
    public String createBranch(Model model) {
        modelMessageService.setModelMessage(model, "branch", new Branch());
        modelMessageService.setEmptyResultMessage(model);
        return "addBranch";
    }

    @PostMapping("/admin/addBranch")
    public String addCustomer(@Valid @ModelAttribute Branch branch, BindingResult bindingResult, Model model, HttpServletRequest request) {
        return addOrUpdateBranch(branch, bindingResult, model, "addBranch", request);
    }

    @GetMapping(value = "/admin/allBranch")
    public String getAllBranch(Model model) {
        List<Branch> branches = branchRepository.findAllByOrderByNameAsc();
        if (branches != null) {
            modelMessageService.setEmptyResultMessage(model);
            modelMessageService.setModelMessage(model, "branches", branches);
        } else {
            modelMessageService.setModelMessage(model, "result", "No branch added yet.");
        }
        return "listBranch";
    }

    @GetMapping("/admin/branch/{action}/id/{branchId}")
    public String deleteOrEditCategory(@PathVariable("branchId") String branchId, @PathVariable("action") String action, Model model) {
        List<Branch> branches = branchService.getAllBranch();
        if (branches.isEmpty()) {
            modelMessageService.setModelMessage(model, "result", "No Branch Yet added");
            return "listBranch";
        }
        modelMessageService.setModelMessage(model, "branches", getAllBranch());

        if (action.equals("edit")) {
            modelMessageService.setModelMessage(model, "result", "Update Category");
            modelMessageService.setModelMessage(model, "branch", branchService.findBranchById(Long.valueOf(branchId)));
            return "updateBranch";
        }
        modelMessageService.setModelMessage(model, "result", "No Category selected to Update or delete");
        return "listBranch";
    }

    @PostMapping("/admin/branch/update")
    public String updateCategory(@Valid @ModelAttribute Branch branch, BindingResult bindingResult, Model model, HttpServletRequest request) {
        List<Branch> branches = branchService.getAllBranchExceptUpdateBranch(branch.getId());
        return addOrUpdateBranch(branch, bindingResult, model, "listBranch", request);
    }

    private String addOrUpdateBranch(Branch branch, BindingResult bindingResult, Model model, String returnString, HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            modelMessageService.setModelMessage(model, "result", "Branch can't be saved with wrong input");
            return returnString;
        }
        ObjectMapper mapper = new ObjectMapper();
        String oldJson = null;
        try {
            oldJson = mapper.writeValueAsString(branch);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        int count = branchRepository.countByName(branch.getName());
        if (count > 0) {
            model.addAttribute("result", "This branch name is already used ");
            return returnString;
        }
        branchService.addBranch(branch);
        if (returnString.equals("listBranch")) {
            try {
                String newJson = mapper.writeValueAsString(branch);
                eventLogService.saveUpdateLog(newJson, oldJson, Branch.class, String.valueOf(branch.getId()), request);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
            modelMessageService.setModelMessage(model, "branches", getAllBranch());
            modelMessageService.setModelMessage(model, "result", "Category updated successfully");
        }
        if (returnString.equals("addBranch")) {
            try {
                String newJson = mapper.writeValueAsString(branch);
                eventLogService.saveInsertLog(newJson, Branch.class, String.valueOf(branch.getId()), request);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
            modelMessageService.setModelMessage(model, "result", "Branch added successfully");
        }
        return returnString;
    }

    private List<Branch> getAllBranch() {
        return branchService.getAllBranch();
    }

    @GetMapping(value = {"/admin/getCustomerByBranch"})
    @ResponseBody
    public List<Customer> getCustomerByBranch(@RequestParam(value = "branchId") Long branchId, HttpServletRequest request) {
        return customerRepository.findAllByBranchIdOrderByCustomerNameAsc(branchId);
    }

    @GetMapping(value = {"/admin/getItemByBranch"})
    @ResponseBody
    public List<Item> getItemByBranch(@RequestParam(value = "branchId") Long branchId, HttpServletRequest request) {
        return itemService.getAllItemByBranchId(branchId);
    }

    @GetMapping(value = {"/admin/getSalespersonByBranch"})
    @ResponseBody
    public List<Salesperson> getSalespersonByBranch(@RequestParam(value = "branchId") Long branchId, HttpServletRequest request, Model model) {
        return salespersonRepository.findAllByBranchIdOrderBySalespersonName(branchId);
    }
}
