package com.printtex.printtex_pos.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.printtex.printtex_pos.model.Salesperson;
import com.printtex.printtex_pos.service.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@Controller
public class SalespersonController {
    private final SalespersonService salespersonService;
    private final ModelMessageService modelMessageService;
    private final BillService billService;
    private final EventLogService eventLogService;
    private final BranchService branchService;

    public SalespersonController(SalespersonService salespersonService, ModelMessageService modelMessageService, BillService billService, EventLogService eventLogService, BranchService branchService) {
        this.salespersonService = salespersonService;
        this.modelMessageService = modelMessageService;
        this.billService = billService;
        this.eventLogService = eventLogService;
        this.branchService = branchService;
    }


    @GetMapping(value = {"/admin/addsalesperson", "/branch/addsalesperson"})
    public String createCustomer(Model model) {
        modelMessageService.setModelMessage(model, "salesperson", new Salesperson());
        modelMessageService.setEmptyResultMessage(model);
        return "addsalesperson";

    }

    @GetMapping(value = {"/admin/allsalespersonbranch/{branchId}"})
    @ResponseBody
    public List<Salesperson> seeAllSalespersonAdmin(@PathVariable Long branchId, Model model) {
        return salespersonService.getAllSalesPersonByBranchId(branchId);
    }

    @PostMapping(value = {"/admin/addsalesperson", "/branch/addsalesperson"})
    public String addSalesperson(@Valid @ModelAttribute Salesperson salesperson, BindingResult bindingResult, Model model, HttpServletRequest request) {
        List<Salesperson> salespersonList = salespersonService.getAll();
        return addOrUpdateSalesperson(salespersonList, salesperson, bindingResult, model, "addsalesperson", request);
    }

    @GetMapping(value = {"/admin/allsalespersonbybranch/{branchId}"})
    @ResponseBody
    public List<Salesperson> seeAllSalespersonsAdmin(@PathVariable Long branchId) {
        return null;
    }

    @GetMapping(value = {"/admin/allsalesperson", "/branch/allsalesperson"})
    public String getSalespersonList(Model model) {
        List<Salesperson> salespersons = salespersonService.getAllSalesPersonByBranch(model);
        if (!salespersons.isEmpty()) {
            modelMessageService.setEmptyResultMessage(model);
            modelMessageService.setModelMessage(model, "salespersons", salespersons);
        } else {
            modelMessageService.setModelMessage(model, "result", "No Salesperson Yet added");
        }
        return "listsalesperson";
    }

    @GetMapping("/admin/salesperson/{action}/id/{salespersonId}")
    public String deleteOrEditSalesperson(@PathVariable("salespersonId") String salespersonId, @PathVariable("action") String action, Model model, HttpServletRequest request) {
        List<Salesperson> salespersons = getSalespersonList();
        if (salespersons.isEmpty()) {
            modelMessageService.setModelMessage(model, "result", "No Salesperson Yet added");
            return "listsalesperson";
        }
        modelMessageService.setModelMessage(model, "salespersons", salespersons);
        if (action.equals("delete")) {
            if (billService.getBillListBySalespersonId(Integer.valueOf(salespersonId), model).size() > 0) {
                modelMessageService.setModelMessage(model, "result", "This salesperson is related to bill and sale, so you can't delete it. You can use update option");
                return "listsalesperson";
            }
            salespersonService.deleteSalesperson(Integer.valueOf(salespersonId), request);
            return "redirect:/admin/allsalesperson";
        }
        if (action.equals("edit")) {
            modelMessageService.setModelMessage(model, "result", "Update Salesperson");
            modelMessageService.setModelMessage(model, "branchList", branchService.getAllBranch());
            modelMessageService.setModelMessage(model, "salesperson", salespersonService.findSalespersonById(Integer.valueOf(salespersonId)));
            return "updatesalesperson";
        }
        modelMessageService.setModelMessage(model, "result", "No Salesperson selected to Update or delete");
        return "listsalesperson";
    }

    @PostMapping("/admin/salesperson/update")
    public String updateSalesperson(@Valid @ModelAttribute Salesperson salesperson, BindingResult bindingResult, Model model, HttpServletRequest request) {
        List<Salesperson> salespersons = salespersonService.getAllSalespersonExceptUpdateSalesperson(salesperson.getId());
        return addOrUpdateSalesperson(salespersons, salesperson, bindingResult, model, "listsalesperson", request);
    }

    @GetMapping("/admin/salesperson/update")
    public String showAllSalespersons() {
        return "redirect:/admin/allsalesperson";
    }

    private String addOrUpdateSalesperson(List<Salesperson> salespersonList, Salesperson salesperson, BindingResult bindingResult, Model model, String returnString, HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            modelMessageService.setModelMessage(model, "result", "Salesperson can't be saved with wrong input");
            return returnString;
        }
        ObjectMapper mapper = new ObjectMapper();
        String oldJson = null;
        try {
            oldJson = mapper.writeValueAsString(salesperson);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        boolean[] existMobileNo = {false};
        salespersonList.forEach(existingSalesperson -> {
            if (existingSalesperson.getMobileNo().equals(salesperson.getMobileNo())) {
                existMobileNo[0] = true;
            }
        });
        if (existMobileNo[0]) {
            model.addAttribute("result", "This mobile number is already used ");
            return returnString;
        }
        salespersonService.addSalesperson(salesperson, request);
        if (returnString.equals("listsalesperson")) {
            try {
                String newJson = mapper.writeValueAsString(salesperson);
                eventLogService.saveUpdateLog(newJson, oldJson, Salesperson.class, String.valueOf(salesperson.getId()), request);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
            modelMessageService.setModelMessage(model, "result", "Salesperson  updated successfully");
            modelMessageService.setModelMessage(model, "salespersons", getSalespersonList());
        }
        if (returnString.equals("addsalesperson")) {
            try {
                String newJson = mapper.writeValueAsString(salesperson);
                eventLogService.saveInsertLog(newJson, Salesperson.class, String.valueOf(salesperson.getId()), request);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
            modelMessageService.setModelMessage(model, "result", "Salesperson added successfully");
        }
        return returnString;
    }

    private List<Salesperson> getSalespersonList() {
        return salespersonService.getAll();
    }
}

