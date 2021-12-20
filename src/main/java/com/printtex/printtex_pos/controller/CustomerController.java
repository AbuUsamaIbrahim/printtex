package com.printtex.printtex_pos.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.printtex.printtex_pos.model.Category;
import com.printtex.printtex_pos.model.Customer;
import com.printtex.printtex_pos.model.CustomerDTO;
import com.printtex.printtex_pos.service.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
public class CustomerController {
    private final CustomerService customerService;
    private final ModelMessageService modelMessageService;
    private final MorePaymentService morePaymentService;
    private final CompanyService companyService;
    private final NumberToWordService numberToWordService;
    private final BillService billService;
    private final EventLogService eventLogService;
    private final CategoryService categoryService;

    public CustomerController(CustomerService customerService, ModelMessageService modelMessageService, MorePaymentService morePaymentService, CompanyService companyService, NumberToWordService numberToWordService, BillService billService, EventLogService eventLogService, LoginController loginController, CategoryService categoryService) {
        this.customerService = customerService;
        this.modelMessageService = modelMessageService;
        this.morePaymentService = morePaymentService;
        this.companyService = companyService;
        this.numberToWordService = numberToWordService;
        this.billService = billService;
        this.eventLogService = eventLogService;
        this.categoryService = categoryService;
    }

    @GetMapping(value = {"/admin/addcustomer", "/branch/addcustomer"})
    public String createCustomer(Model model) {
        modelMessageService.setModelMessage(model, "customer", new Customer());
        modelMessageService.setEmptyResultMessage(model);
        return "addcustomer";
    }

    @PostMapping(value = {"/admin/addcustomer", "/branch/addcustomer"})
    public String addCustomer(@Valid @ModelAttribute Customer customer, BindingResult bindingResult, Model model, HttpServletRequest request) {
        return addOrUpdateCustomer(getAllCustomers(model), customer, bindingResult, model, "addcustomer", request);
    }

    @GetMapping(value = {"/admin/allcustomer", "/branch/allcustomer"})
    public String getCustomerList(Model model) {
        return allCustomerOrUpdateDue(model, "listcustomer", getAllCustomers(model));
    }

    @GetMapping(value = {"/admin/allcustomer/due", "/branch/allcustomer/due"})
    public String getCustomerDueList(Model model) {
        return allCustomerOrUpdateDue(model, "duepayments", getAllCustomers(model));
    }

    @GetMapping(value = {"/admin/allcustomerbranch/{branchId}"})
    @ResponseBody
    public List<Customer> seeAllCustomerAdmin(@PathVariable Long branchId, Model model) {
        return customerService.getAllCustomerByBranch(branchId);
    }


    @GetMapping(value = {"/admin/allcustomerbybranch/{branchId}"})
    @ResponseBody
    public List<Customer> seeAllCustomersAdmin(@PathVariable Long branchId) {
        return customerService.getAllCustomerByBranch(branchId);
    }

    @GetMapping(value = "/admin/testFont")
    @ResponseBody
    public List<Category> getAllCategory() {
        return categoryService.getAllCategory();
    }

    @GetMapping("/admin/customer/{action}/id/{customerId}")
    public String deleteOrEditCustomer(@PathVariable("customerId") String customerId, @PathVariable("action") String action, Model model, HttpServletRequest request) {
        List<Customer> customers = getAllCustomers(model);
        modelMessageService.setModelMessage(model, "customers", customers);
        if (customers.isEmpty()) {
            modelMessageService.setModelMessage(model, "result", "No Customer Yet added");
            return "listcustomer";
        }
        if (action.equals("delete")) {
            if (billService.getBillListByCustomerId(Integer.parseInt(customerId), model).size() > 0) {
                modelMessageService.setModelMessage(model, "result", "This customer is related to bill and sale, so you can't delete it.  You can use update option");
                return "listcustomer";
            }
            customerService.deleteCustomer(Integer.parseInt(customerId), request);
            return "redirect:/admin/allcustomer";
        }
        if (action.equals("edit")) {
            modelMessageService.setModelMessage(model, "result", "Update Customer");
            modelMessageService.setModelMessage(model, "customer", customerService.getCustomerByCustomerId(Integer.valueOf(customerId)));
            return "updatecustomer";
        }
        modelMessageService.setModelMessage(model, "result", "No Customer selected to Update or delete");
        return "listcustomer";
    }

    @GetMapping(value = {"/admin/due/payment/customer/id/{customerId}", "/branch/due/payment/customer/id/{customerId}"})
    public String duePayment(@PathVariable("customerId") String customerId, Model model) {
        Customer customer = customerService.getCustomerByCustomerId(Integer.parseInt(customerId));
        modelMessageService.setEmptyResultMessage(model);
        modelMessageService.setModelMessage(model, "customer", customer);
        return "paiddues";
    }

    @PostMapping("/admin/customer/update_due")
    public String updateCustomerPayment(@Valid @ModelAttribute Customer customer, BindingResult bindingResult, Model model, HttpServletRequest request) {
        List<Customer> customers = customerService.getAllCustomerExceptUpdateCustomer(customer.getId());
        return addOrUpdateCustomer(customers, customer, bindingResult, model, "duepaidstatement", request);

    }

    @PostMapping("/admin/customer/update")
    public String updateCustomer(@Valid @ModelAttribute Customer customer, BindingResult bindingResult, Model model, HttpServletRequest request) {
        List<Customer> customers = customerService.getAllCustomerExceptUpdateCustomer(customer.getId());
        return addOrUpdateCustomer(customers, customer, bindingResult, model, "listcustomer", request);
    }

    private String addOrUpdateCustomer(List<Customer> customerList, Customer customer, BindingResult bindingResult, Model model, String returnString, HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            modelMessageService.setModelMessage(model, "result", "Customer can't be saved with wrong input");
            return returnString;
        }
        ObjectMapper mapper = new ObjectMapper();
        String oldJson = null;
        try {
            oldJson = mapper.writeValueAsString(customer);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        boolean[] existMobileNo = {false};
        customerList.forEach(existingCustomer -> {
            if (existingCustomer.getMobileNo().equals(customer.getMobileNo())) {
                existMobileNo[0] = true;
            }
        });
        if (existMobileNo[0]) {
            model.addAttribute("result", "This mobile number is already used ");
            return returnString;
        }
        if (returnString.equals("duepaidstatement")) {
            Customer paidDueCustomer = customerService.getCustomerByCustomerId(customer.getId());
            double previousDue = paidDueCustomer.getPreviousDueAmount();
            String companyAddress = companyService.getCompanyList().get(0).getCompanyAddress();
            double paidDue = customer.getPreviousDueAmount();
            double newDue = morePaymentService.getNewDue(model, previousDue, paidDue);
            customer.setPreviousDueAmount(newDue);
            modelMessageService.setModelMessage(model, "result", "Received due successfully");
            modelMessageService.setModelMessage(model, "newDue", newDue);
            modelMessageService.setModelMessage(model, "previousDue", previousDue);
            modelMessageService.setModelMessage(model, "date", new Date());
            modelMessageService.setModelMessage(model, "paidDueAmount", paidDue);
            modelMessageService.setModelMessage(model, "companyAddress", companyAddress);
            modelMessageService.setModelMessage(model, "customerName", customer.getCustomerName());
            modelMessageService.setModelMessage(model, "customerAddress", customer.getAddress());
        }
        customerService.addCustomer(customer);


        if (returnString.equals("listcustomer")) {
            try {
                String userNewJson = mapper.writeValueAsString(customer);
                eventLogService.saveUpdateLog(userNewJson, oldJson, Customer.class, String.valueOf(customer.getId()), request);
            } catch (JsonProcessingException e) {

            }
            modelMessageService.setModelMessage(model, "customers", getCustomerDTOList(getAllCustomers(model)));
            modelMessageService.setModelMessage(model, "result", "Customer updated successfully");
        }
        if (returnString.equals("addcustomer")) {
            try {
                String userNewJson = mapper.writeValueAsString(customer);
                eventLogService.saveInsertLog(userNewJson, Customer.class, String.valueOf(customer.getId()), request);
            } catch (JsonProcessingException e) {

            }
            modelMessageService.setModelMessage(model, "result", "Customer added successfully");
        }
        return returnString;
    }

    @GetMapping("/admin/customer/update")
    public String allCustomers() {
        return "redirect:/admin/allcustomer";
    }

    private String allCustomerOrUpdateDue(Model model, String returnString, List<Customer> customers) {
        if (!customers.isEmpty()) {
            modelMessageService.setEmptyResultMessage(model);
            modelMessageService.setModelMessage(model, "customers", getCustomerDTOList(getAllCustomers(model)));
        } else {
            modelMessageService.setModelMessage(model, "result", "No Customer Yet added");
        }
        return returnString;
    }

    private List<CustomerDTO> getCustomerDTOList(List<Customer> customerList) {
        List<CustomerDTO> customerDTOList = new ArrayList<>();
        customerList.forEach(customer1 -> {
            CustomerDTO customerDTO = new CustomerDTO();
            customerDTO.setId(customer1.getId());
            customerDTO.setCustomerName(customer1.getCustomerName());
            customerDTO.setCustomerPreviousBillNo(customer1.getCustomerPreviousBillNo());
            customerDTO.setMobileNo(customer1.getMobileNo());
            customerDTO.setAddress(customer1.getAddress());
            customerDTO.setPreviousDueAmount(numberToWordService.castDoubleToString(customer1.getPreviousDueAmount()));
            customerDTOList.add(customerDTO);
        });
        return customerDTOList;
    }

    private List<Customer> getAllCustomers(Model model) {
        return customerService.getAllCustomer(model);
    }


}
