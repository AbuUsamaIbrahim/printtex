package com.printtex.printtex_pos.controller;

import com.printtex.printtex_pos.model.*;
import com.printtex.printtex_pos.service.*;
import com.printtex.printtex_pos.util.DateUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class SaleController {
    private final SaleService saleService;
    private final CompanyService companyService;
    private final ItemService itemService;
    private final CustomerService customerService;
    private final SalespersonService salespersonService;
    private final ModelMessageService modelMessageService;
    private final MorePaymentService morePaymentService;
    private final BillService billService;
    private final NumberToWordService numberToWordService;
    private final LoginController loginController;
    private List<Sale> recentSaleList = new ArrayList<>();

    public SaleController(LoginController loginController, SaleService saleService, CompanyService companyService, ItemService itemService, CustomerService customerService, SalespersonService salespersonService, ModelMessageService modelMessageService, MorePaymentService morePaymentService, BillService billService, NumberToWordService numberToWordService) {
        this.loginController = loginController;
        this.saleService = saleService;
        this.companyService = companyService;
        this.itemService = itemService;
        this.customerService = customerService;
        this.salespersonService = salespersonService;
        this.modelMessageService = modelMessageService;
        this.morePaymentService = morePaymentService;
        this.billService = billService;
        this.numberToWordService = numberToWordService;
    }

    @GetMapping(value = {"/admin/sale", "/admin/addsale", "branch/sale", "branch/addsale"})
    public String createSale(Model model) {
        modelMessageService.setEmptyResultMessage(model);
        return showSale(model);
    }

    @PostMapping(value = {"/admin/addsale", "/branch/addsale"})
    public String addSale(@Valid @ModelAttribute Sale sale, BindingResult bindingResult, Model model, HttpServletRequest request) {
        if (bindingResult.hasErrors() || sale.getCompany().getCompanyId() == 0 || sale.getCustomer().getId() == 0 || sale.getSalesperson().getId() == 0 || sale.getSaleUnitPrice() == 0 || sale.getItem().getItemId() == 0 || sale.getSaleAmount() == 0 || sale.getSaleDrumNo() == 0) {
            modelMessageService.setModelMessage(model, "result", "This item can't be sale with wrong input");
            return showSale(model);
        }
        Item item = itemService.findItemByItemId(sale.getItem().getItemId());
        Company company = companyService.getCompanyByCompanyId(sale.getCompany().getCompanyId());
        Salesperson salesperson = salespersonService.findSalespersonById(sale.getSalesperson().getId());
        Customer customer = customerService.getCustomerByCustomerId(sale.getCustomer().getId());
        if (item.getItemAmount() < sale.getSaleAmount()) {
            modelMessageService.setModelMessage(model, "result", "You can't make sale. because you have only " + item.getItemAmount() + " Kg " + item.getItemName());
            return showSale(model);
        }
        item.setItemUnitPrice(sale.getSaleUnitPrice());
        int existingItemAmount = item.getItemAmount() - sale.getSaleAmount();
        item.setItemAmount(existingItemAmount);
        itemService.addItem(item, request);
        sale.setItem(item);
        sale.setCompany(company);
        sale.setCustomer(customer);

        sale.setSalesperson(salesperson);
        sale.setSaleDate(new Date());

        double totalSaleAmount = sale.getSaleAmount() * sale.getSaleUnitPrice();
        sale.setTotalSaleAmount(totalSaleAmount);
        recentSaleList.add(sale);
        saleService.addSale(sale, request);
        User user = loginController.getAuthenticatedUser();
        modelMessageService.setModelMessage(model, "result", "This Sale is successful.");
        modelMessageService.setModelMessage(model, "saleList", recentSaleList.stream().filter(recentSale -> recentSale.getBranchId().equals(user.getBranch().getId()) && recentSale.getUserId().equals(Long.valueOf(String.valueOf(user.getId())))).collect(Collectors.toList()));
        return "listsale";
    }

    @GetMapping({"/admin/sale/{action}/id/{saleId}", "/branch/sale/{action}/id/{saleId}"})
    public String deleteOrEditSale(@PathVariable("saleId") Integer saleId, @PathVariable("action") String action, Model model, HttpServletRequest request) {
        List<Sale> sales = saleService.getAllSale();
        if (sales.isEmpty()) {
            modelMessageService.setModelMessage(model, "result", "No Sales Yet added");
            return showSale(model);
        }
        if (action.equals("delete")) {

            Sale sale = saleService.getSaleBySaleId(Integer.valueOf(saleId));
            Item item = itemService.findItemByItemId(sale.getItem().getItemId());
            int reAddAmount = item.getItemAmount() + sale.getSaleAmount();
            item.setItemAmount(reAddAmount);
            itemService.addItem(item, request);
            saleService.deleteSale(Integer.valueOf(saleId), request);
            User user = loginController.getAuthenticatedUser();
            recentSaleList.stream().filter(recentSale -> recentSale.getBranchId().equals(user.getBranch().getId()) && recentSale.getUserId().equals(Long.valueOf(String.valueOf(user.getId())))).collect(Collectors.toList()).removeIf(p -> p.getSaleId() == saleId);
            modelMessageService.setModelMessage(model, "saleList", recentSaleList.stream().filter(recentSale -> recentSale.getBranchId().equals(user.getBranch().getId()) && recentSale.getUserId().equals(Long.valueOf(String.valueOf(user.getId())))).collect(Collectors.toList()));
            modelMessageService.setModelMessage(model, "result", "Selected sale deleted");
            return "listsale";
        }
        modelMessageService.setModelMessage(model, "result", "No Sale selected to Update or delete");
        return "listsale";
    }

    @GetMapping({"/admin/payment", "/branch/payment"})
    public String makePayment(Model model, HttpServletRequest request) {
        User user = loginController.getAuthenticatedUser();
        List<Sale> saleList = recentSaleList.stream().filter(recentSale -> recentSale.getBranchId().equals(user.getBranch().getId()) && recentSale.getUserId().equals(Long.valueOf(String.valueOf(user.getId())))).collect(Collectors.toList());
        if (saleList.isEmpty()) {
            if (request.getRequestURI().contains("/admin/")) {
                return "redirect:/admin/sale";
            }
            return "redirect:/branch/sale";
        }
        modelMessageService.setModelMessage(model, "payment", new Payment());
        modelMessageService.setEmptyResultMessage(model);
        modelMessageService.setModelMessage(model, "total", numberToWordService.addCommasToNumber(getTotal()));
        modelMessageService.setModelMessage(model, "previousDue", numberToWordService.addCommasToNumber(getPreviousDue(recentSaleList.stream().filter(recentSale -> recentSale.getBranchId().equals(user.getBranch().getId()) && recentSale.getUserId().equals(Long.valueOf(String.valueOf(user.getId())))).collect(Collectors.toList()).get(0).getCustomer())));
        modelMessageService.setModelMessage(model, "totalWithDue", numberToWordService.addCommasToNumber(recentSaleList.stream().filter(recentSale -> recentSale.getBranchId().equals(user.getBranch().getId()) && recentSale.getUserId().equals(Long.valueOf(String.valueOf(user.getId())))).collect(Collectors.toList()).get(0).getCustomer().getPreviousDueAmount() + getTotal()));
        if (request.getRequestURI().contains("/admin/")) {
            return "payment";
        }
        return "payment";
    }


    @PostMapping({"/admin/bill", "/branch/bill"})
    public String makeBillAndChallan(@Valid @ModelAttribute Payment payment, BindingResult bindingResult, Model model, HttpServletResponse response, HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            if (request.getRequestURI().contains("/admin/")) {
                return "payment";
            }
            return "branch/payment";
        }
        User user = loginController.getAuthenticatedUser();
        if (recentSaleList.stream().filter(recentSale -> recentSale.getBranchId().equals(user.getBranch().getId()) && recentSale.getUserId().equals(Long.valueOf(String.valueOf(user.getId())))).collect(Collectors.toList()).isEmpty()) {
            if (request.getRequestURI().contains("/admin/")) {
                return "redirect:/admin/sale";
            }
            return "redirect:/branch/sale";
        }

        recentSaleList.stream().filter(recentSale -> recentSale.getBranchId().equals(user.getBranch().getId()) && recentSale.getUserId().equals(Long.valueOf(String.valueOf(user.getId())))).collect(Collectors.toList()).forEach(sale -> {
            sale.setSaleStatus("billed");
            saleService.addSale(sale, request);
        });

        if (saleService.getDraftSales().size() > 0) {
            saleService.getDraftSales().forEach(sale -> {
                Item item = itemService.findItemByItemId(sale.getItem().getItemId());
                int reAddAmount = item.getItemAmount() + sale.getSaleAmount();
                item.setItemAmount(reAddAmount);
                itemService.addItem(item, request);
            });
            saleService.deleteDraftSales();
        }

        List<Sale> saleList = new ArrayList<>(recentSaleList.stream().filter(recentSale -> recentSale.getBranchId().equals(user.getBranch().getId()) && recentSale.getUserId().equals(Long.valueOf(String.valueOf(user.getId())))).collect(Collectors.toList()));
        final int[] totalQty = {0};
        saleList.forEach(sale -> {
            totalQty[0] += sale.getSaleAmount();
        });
        Customer customer = getRecentCustomer(saleList);
        double paidAmount = payment.getPaidPayment();
        int billNo = customer.getCustomerPreviousBillNo() + 1;
        double previousDue = getPreviousDue(customer);
        double totalWithPreviousDue = getTotal() + previousDue;
        ZoneId zoneId = ZoneId.of("Asia/Dhaka");
        double newDue = morePaymentService.getNewDue(model, totalWithPreviousDue, paidAmount);
        customer.setPreviousDueAmount(newDue);
        customer.setCustomerPreviousBillNo(billNo);
        customerService.addCustomer(customer);
        modelMessageService.setEmptyResultMessage(model);
        String viewBillNo = billNo + "/" + LocalDate.now(zoneId).getYear();
        Bill bill = new Bill();
        bill.setBillingDate(new Date());
        bill.setBillNo(viewBillNo);
        bill.setCustomer(customer);
        bill.setPaidAmount(paidAmount);
        bill.setPaymentDate(payment.getPaymentDate());
        bill.setPreviousDueAmount(previousDue);
        bill.setNewDueAmount(newDue);
        bill.setTotalPayableAmount(totalWithPreviousDue);
        bill.setTotalAmount(getTotal());
        bill.setSalesperson(recentSaleList.stream().filter(recentSale -> recentSale.getBranchId().equals(user.getBranch().getId()) && recentSale.getUserId().equals(Long.valueOf(String.valueOf(user.getId())))).collect(Collectors.toList()).get(0).getSalesperson());
        bill.setTotalQuantity(totalQty[0]);
        bill.setCompany(recentSaleList.stream().filter(recentSale -> recentSale.getBranchId().equals(user.getBranch().getId()) && recentSale.getUserId().equals(Long.valueOf(String.valueOf(user.getId())))).collect(Collectors.toList()).get(0).getCompany());
        bill.setSales(recentSaleList.stream().filter(recentSale -> recentSale.getBranchId().equals(user.getBranch().getId()) && recentSale.getUserId().equals(Long.valueOf(String.valueOf(user.getId())))).collect(Collectors.toList()));
        bill = billService.saveBill(bill);
        Bill finalBill = bill;
        recentSaleList.stream().filter(recentSale -> recentSale.getBranchId().equals(user.getBranch().getId()) && recentSale.getUserId().equals(Long.valueOf(String.valueOf(user.getId())))).collect(Collectors.toList()).forEach(sale -> {
            sale.setBill(finalBill);
            saleService.addSale(sale, request);
        });
        recentSaleList.stream().filter(recentSale -> recentSale.getBranchId().equals(user.getBranch().getId()) && recentSale.getUserId().equals(Long.valueOf(String.valueOf(user.getId())))).collect(Collectors.toList()).forEach(sale -> {
            recentSaleList.remove(sale);
        });
        modelMessageService.setModelMessage(model, "billId", bill.getBillId());
        if (request.getRequestURI().contains("/admin/")) {

            return "billandchallan";
        }
        return "billandchallan";
    }

    private double getPreviousDue(Customer customer) {
        return customer.getPreviousDueAmount();
    }

    private double getTotal() {
        User user = loginController.getAuthenticatedUser();
        final double[] total = {0.0};
        recentSaleList.stream().filter(recentSale -> recentSale.getBranchId().equals(user.getBranch().getId()) && recentSale.getUserId().equals(Long.valueOf(String.valueOf(user.getId())))).collect(Collectors.toList()).forEach(sale -> {
            total[0] += sale.getTotalSaleAmount();
        });
        return total[0];
    }

    private Customer getRecentCustomer(List<Sale> sales) {
        return sales.get(0).getCustomer();
    }

    private String showSale(Model model) {
        List<Item> items = itemService.getAllItem();
        List<Salesperson> salespersons = salespersonService.getAllSalesPersonByBranch(model);
        User user = loginController.getAuthenticatedUser();
        List<Customer> customers = customerService.getAllCustomerByBranch(user.getBranch().getId());
        List<Company> companies = companyService.getCompanyList();
        try {
            modelMessageService.setModelMessage(model, "saleList", recentSaleList.stream().filter(recentSale -> recentSale.getBranchId().equals(user.getBranch().getId()) && recentSale.getUserId().equals(Long.valueOf(String.valueOf(user.getId())))).collect(Collectors.toList()));
        } catch (Exception e) {
            modelMessageService.setModelMessage(model, "saleList", new ArrayList<Sale>());
        }

        modelMessageService.setModelMessage(model, "items", items);
        Sale sale = new Sale();
        sale.setSaleStatus("draft");
        try {
            if (recentSaleList.stream().filter(recentSale -> recentSale.getBranchId().equals(user.getBranch().getId()) && recentSale.getUserId().equals(Long.valueOf(String.valueOf(user.getId())))).collect(Collectors.toList()).isEmpty()) {
                modelMessageService.setModelMessage(model, "customers", customers);
                modelMessageService.setModelMessage(model, "salespersons", salespersons);
                modelMessageService.setModelMessage(model, "companies", companies);
                modelMessageService.setModelMessage(model, "sale", sale);
                return "addsale";
            }
            sale.setSalesperson(salespersonService.findSalespersonById(recentSaleList.stream().filter(recentSale -> recentSale.getBranchId().equals(user.getBranch().getId()) && recentSale.getUserId().equals(Long.valueOf(String.valueOf(user.getId())))).collect(Collectors.toList()).get(0).getSalesperson().getId()));
            sale.setCustomer(customerService.getCustomerByCustomerId(recentSaleList.stream().filter(recentSale -> recentSale.getBranchId().equals(user.getBranch().getId()) && recentSale.getUserId().equals(Long.valueOf(String.valueOf(user.getId())))).collect(Collectors.toList()).get(0).getCustomer().getId()));
            sale.setCompany(companyService.getCompanyByCompanyId(recentSaleList.stream().filter(recentSale -> recentSale.getBranchId().equals(user.getBranch().getId()) && recentSale.getUserId().equals(Long.valueOf(String.valueOf(user.getId())))).collect(Collectors.toList()).get(0).getCompany().getCompanyId()));
            sale.setDrumOrCartoon(saleService.getSaleBySaleId(recentSaleList.stream().filter(recentSale -> recentSale.getBranchId().equals(user.getBranch().getId()) && recentSale.getUserId().equals(Long.valueOf(String.valueOf(user.getId())))).collect(Collectors.toList()).get(0).getSaleId()).getDrumOrCartoon());
        } catch (Exception e) {

        }
        modelMessageService.setModelMessage(model, "sale", sale);
        return "addsale";
    }
}
