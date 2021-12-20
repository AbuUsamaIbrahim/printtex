package com.printtex.printtex_pos.controller;

import com.printtex.printtex_pos.dto.Response;
import com.printtex.printtex_pos.model.*;
import com.printtex.printtex_pos.repository.ItemSaleBranchRepository;
import com.printtex.printtex_pos.service.*;

import java.io.IOException;
import java.io.PrintWriter;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@Controller
public class BillController {

    private final BillService billService;
    private final SaleService saleService;
    private final ModelMessageService modelMessageService;
    private final CustomerService customerService;
    private final NumberToWordService numberToWordService;
    private final ItemService itemService;
    private final SalespersonService salespersonService;
    private final LoginController loginController;
    private final BranchService branchService;
    private final ItemSaleBranchRepository itemSaleBranchRepository;

    private Report report = null;

    public BillController(BillService billService, SaleService saleService, ModelMessageService modelMessageService, CustomerService customerService, NumberToWordService numberToWordService, ItemService itemService, SalespersonService salespersonService, LoginController loginController, BranchService branchService, ItemSaleBranchRepository itemSaleBranchRepository) {
        this.billService = billService;
        this.saleService = saleService;
        this.modelMessageService = modelMessageService;
        this.customerService = customerService;
        this.numberToWordService = numberToWordService;
        this.itemService = itemService;
        this.salespersonService = salespersonService;
        this.loginController = loginController;
        this.branchService = branchService;
        this.itemSaleBranchRepository = itemSaleBranchRepository;
    }

    @GetMapping(value = {"/admin/bills", "/branch/bills"})
    public String billView(Model model) {
        return "billview";
    }

    @GetMapping(value = {"/admin/bill/all", "/branch/bill/all"})
    public String seeAllBill(Model model) {

        modelMessageService.setModelMessage(model, "heading", "Previous Bill Records");

        return "allbill";


    }

    @PostMapping(value = {"/admin/bill/all", "/branch/bill/all"})
    @ResponseBody
    public String seeAllBillByPost(Model model, HttpServletRequest request, HttpServletResponse response) throws IOException {

        Integer pageNumber = request.getParameter("start") != null ? Integer.parseInt(request.getParameter("start")) : 0;
        Integer length = request.getParameter("length") != null ? Integer.parseInt(request.getParameter("length")) : 10;
        Integer draw = request.getParameter("draw") != null ? Integer.parseInt(request.getParameter("draw")) : 1;
        Pageable p = new PageRequest(pageNumber / length, length, new Sort(Sort.Direction.DESC, "billId", "billingDate"));
        User user = loginController.getAuthenticatedUser();
        List<Bill> list = new ArrayList<>();
        if (user.getRole().equals("1")) {
            list = billService.getBillList(p);
            globalPaginationClass(model, list, true, response, draw, billService.getTotalCount(), 1);
            return null;
        }
        list = billService.getBillListByBranch(p, user.getBranch().getId());
        globalPaginationClassForBranch(list, true, response, draw, billService.getTotalCountByBranch(user.getBranch().getId()), 1);
        return null;
    }

    private void globalPaginationClassForBranch(List<Bill> list, boolean notReport, HttpServletResponse response, Integer draw, Integer totalcount, Integer condition) throws IOException {
        JSONObject jobject = new JSONObject();
        JSONArray jArray = new JSONArray();
        for (Bill bill : list) {
            JSONArray ja = new JSONArray();
            if (bill.getBillNo() != null && bill.getBillNo().length() > 0) {
                ja.put(bill.getBillNo());
            } else {
                ja.put("N/A");
            }
            if (bill.getBillingDate() != null) {
                ja.put(String.valueOf(bill.getBillingDate().toLocaleString()).substring(0, 12));
            } else {
                ja.put("N/A");
            }
            if (condition == 1) {
                if (bill.getCustomer() != null && bill.getCustomer().getCustomerName() != null && bill.getCustomer().getCustomerName().length() > 0) {
                    ja.put(bill.getCustomer().getCustomerName());
                } else {
                    ja.put("N/A");
                }
            }


            ja.put(String.valueOf(new BigDecimal(String.valueOf(bill.getTotalAmount())).intValue()));
            ja.put(String.valueOf(new BigDecimal(String.valueOf(bill.getPreviousDueAmount())).intValue()));
            ja.put(String.valueOf(new BigDecimal(String.valueOf(bill.getTotalPayableAmount())).intValue()));
            ja.put(String.valueOf(new BigDecimal(String.valueOf(bill.getPaidAmount())).intValue()));
            ja.put(String.valueOf(new BigDecimal(String.valueOf(bill.getNewDueAmount())).intValue()));
            if (notReport) {
                Object o;
//                o = "<button class=\"btn btn-success\" onclick=\"downloadBillReport(" + bill.getBillId() + ")\">Details</button>";
                o = "<a class=\"btn btn-success\" target=\"_blank\" href=\"/admin/downloadBillChallan?bill_id=" + bill.getBillId() + "\">Details</a> <a class=\"btn btn-danger\" onclick=\"return confirm('Are you sure you want to delete this transaction?')\" href=\"/admin/bill/delete/id/" + bill.getBillId() + "\">Remove</a>";
                ja.put(o.toString());

            }
            jArray.put(ja);
        }
        jobject.put("recordsTotal", totalcount);
        jobject.put("recordsFiltered", totalcount);
        jobject.put("data", jArray);
        jobject.put("draw", draw);
        response.setContentType("application/json");
        response.setHeader("Cache-Control", "no-store");
        PrintWriter out = response.getWriter();
        out.print(jobject);

    }

    private void globalPaginationClass(Model model, List<Bill> list, boolean notReport, HttpServletResponse response, Integer draw, Integer totalcount, Integer condition) throws IOException {
        JSONObject jobject = new JSONObject();
        JSONArray jArray = new JSONArray();
        for (Bill bill : list) {
            JSONArray ja = new JSONArray();
            if (bill.getBillNo() != null && bill.getBillNo().length() > 0) {
                ja.put(bill.getBillNo());
            } else {
                ja.put("N/A");
            }
            if (bill.getBillingDate() != null) {
                ja.put(String.valueOf(bill.getBillingDate().toLocaleString()).substring(0, 12));
            } else {
                ja.put("N/A");
            }
            if (condition == 1) {
                if (bill.getCustomer() != null && bill.getCustomer().getCustomerName() != null && bill.getCustomer().getCustomerName().length() > 0) {
                    ja.put(bill.getCustomer().getCustomerName());
                } else {
                    ja.put("N/A");
                }
            }


            ja.put(String.valueOf(new BigDecimal(String.valueOf(bill.getTotalAmount())).intValue()));
            ja.put(String.valueOf(new BigDecimal(String.valueOf(bill.getPreviousDueAmount())).intValue()));
            ja.put(String.valueOf(new BigDecimal(String.valueOf(bill.getTotalPayableAmount())).intValue()));
            ja.put(String.valueOf(new BigDecimal(String.valueOf(bill.getPaidAmount())).intValue()));
            ja.put(String.valueOf(new BigDecimal(String.valueOf(bill.getNewDueAmount())).intValue()));
            if (notReport) {
                Object o;

                if (loginController.isBranch(model)) {
                    o = "<a class=\"btn btn-success\" target=\"_blank\" href=\"/branch/downloadBillChallan?bill_id=" + bill.getBillId() + "\">Details</a> ";
//                    o = "<button class=\"btn btn-success\" onclick=\"downloadBillReport(" + bill.getBillId() + ")\">Details</button>";
                } else {
                    o = "<a class=\"btn btn-success\" target=\"_blank\" href=\"/admin/downloadBillChallan?bill_id=" + bill.getBillId() + "\">Details</a> <a class=\"btn btn-danger\" onclick=\"return confirm('Are you sure you want to delete this transaction?')\" href=\"/admin/bill/delete/id/" + bill.getBillId() + "\">Remove</a>";
//                    o = "<button class=\"btn btn-success\" onclick=\"downloadBillReport(" + bill.getBillId() + ")\">Details</button> <a class=\"btn btn-danger\" onclick=\"return confirm('Are you sure you want to delete this transaction?')\" href=\"/admin/bill/delete/id/" + bill.getBillId() + "\">Remove</a>";
                }
                ja.put(o.toString());

            }
            jArray.put(ja);
        }
        jobject.put("recordsTotal", totalcount);
        jobject.put("recordsFiltered", totalcount);
        jobject.put("data", jArray);
        jobject.put("draw", draw);
        response.setContentType("application/json");
        response.setHeader("Cache-Control", "no-store");
        response.reset();
        PrintWriter out = response.getWriter();
        out.print(jobject);

    }

    @GetMapping(value = {"/admin/bill/by_branch"})
    public String seeBillByBranchView(Model model) {
        modelMessageService.setEmptyResultMessage(model);
        return showBillingBranch(model);
    }

    @PostMapping(value = {"/admin/bill/by_branch"})
    public String seeBillByBranch(Model model, @Valid @ModelAttribute Report report) {
        if (report.getBranch().getId() == 0) {
            modelMessageService.setModelMessage(model, "result", "Can't proceed with wrong input, Please check the field");
            return showBillingBranch(model);
        }
        this.report = null;
        this.report = report;
        modelMessageService.setEmptyResultMessage(model);
//        List<Bill> billList = billService.getBillListByCustomerId(report.getCustomer().getId());
        modelMessageService.setModelMessage(model, "heading", "Previous Bill By Branch");
        modelMessageService.setModelMessage(model, "name", branchService.findBranchById(report.getBranch().getId()).getName());
//        modelMessageService.setModelMessage(model, "allBillByCustomer", getBillDTOList(billList));
        return "allbillbybranch";
    }

    @GetMapping(value = {"/admin/bill/by_customer", "/branch/bill/by_customer"})
    public String seeBillByCustomerView(Model model) {
        modelMessageService.setEmptyResultMessage(model);
        return showBillingCustomer(model);
    }

    @GetMapping(value = {"/admin/bill/by_customer_new", "/branch/bill/by_customer_new"})
    public String seeAllBillByCustomer(Model model) {

        modelMessageService.setModelMessage(model, "heading", "Previous Bill Records");

        return "allbillbycustomer";


    }

    @PostMapping(value = {"/admin/bill/by_customer", "/branch/bill/by_customer"})
    public String seeBillByCustomer(Model model, @Valid @ModelAttribute Report report) {
        if (report.getCustomer().getId() == 0) {
            modelMessageService.setModelMessage(model, "result", "Can't proceed with wrong input, Please check the field");
            return showBillingCustomer(model);
        }
        this.report = null;
        this.report = report;
        modelMessageService.setEmptyResultMessage(model);
//        List<Bill> billList = billService.getBillListByCustomerId(report.getCustomer().getId());
        modelMessageService.setModelMessage(model, "heading", "Previous Bill By Customer");
        modelMessageService.setModelMessage(model, "name", customerService.getCustomerByCustomerId(report.getCustomer().getId()).getCustomerName());
//        modelMessageService.setModelMessage(model, "allBillByCustomer", getBillDTOList(billList));
        return "allbillbycustomer";
    }

    @PostMapping(value = {"/admin/bill/by_customer_p", "/branch/bill/by_customer_p"})
    @ResponseBody
    public String seeBillByCustomerPagination(Model model, HttpServletRequest request, HttpServletResponse response) throws IOException {
        Integer pageNumber = request.getParameter("start") != null ? Integer.parseInt(request.getParameter("start")) : 0;
        Integer length = request.getParameter("length") != null ? Integer.parseInt(request.getParameter("length")) : 10;
        Integer draw = request.getParameter("draw") != null ? Integer.parseInt(request.getParameter("draw")) : 1;

        Pageable p = new PageRequest(pageNumber / length, length, new Sort(Sort.Direction.DESC, "billId", "billingDate"));
        List<Bill> list = billService.getBillListByCustomer(p, report.getCustomer().getId());//asen apni ?hmm cmd chapen
        globalPaginationClass(model, list, true, response, draw, billService.getTotalCountByCustomer(report.getCustomer().getId()), 0);
        return null;
    }

    @PostMapping("/admin/bill/by_branch_p")
    @ResponseBody
    public String seeBillByBranchPagination(Model model, HttpServletRequest request, HttpServletResponse response) throws IOException {
        Integer pageNumber = request.getParameter("start") != null ? Integer.parseInt(request.getParameter("start")) : 0;
        Integer length = request.getParameter("length") != null ? Integer.parseInt(request.getParameter("length")) : 10;
        Integer draw = request.getParameter("draw") != null ? Integer.parseInt(request.getParameter("draw")) : 1;

        Pageable p = new PageRequest(pageNumber / length, length, new Sort(Sort.Direction.DESC, "billId", "billingDate"));
        List<Bill> list = billService.getBillListByBranch(p, report.getBranch().getId());//asen apni ?hmm cmd chapen
        globalPaginationClass(model, list, true, response, draw, billService.getTotalCountByBranch(report.getBranch().getId()), 0);
        return null;
    }

    private String showBillingCustomer(Model model) {
        List<Customer> customers = customerService.getAllCustomer(model);
        if (loginController.isBranch(model)) {
            User user = loginController.getAuthenticatedUser();
            List<Customer> customerList = customerService.getAllCustomerByBranch(user.getBranch().getId());
            modelMessageService.setModelMessage(model, "customers", customerList);
            modelMessageService.setModelMessage(model, "report", new Report());
            modelMessageService.setModelMessage(model, "heading", "Search Previous Billing Details By Customer");
            return "customerbillview";

        }
        modelMessageService.setModelMessage(model, "customers", customers);
        modelMessageService.setModelMessage(model, "report", new Report());
        modelMessageService.setModelMessage(model, "heading", "Search Previous Billing Details By Customer");
        return "customerbillview";
    }

    private String showBillingBranch(Model model) {
        List<Branch> branches = branchService.getAllBranch();
        modelMessageService.setModelMessage(model, "branches", branches);
        modelMessageService.setModelMessage(model, "report", new Report());
        modelMessageService.setModelMessage(model, "heading", "Search Previous Billing Details By Branch");
        return "branchbillview";
    }

    @Transactional
    @GetMapping(value = {"/admin/bill/{action}/id/{billId}", "/branch/bill/{action}/id/{billId}"})
    public String showDetailsBillById(@PathVariable("billId") String billId, @PathVariable("action") String action, Model model, HttpServletRequest request) {
        List<Bill> bills = getAllBills();
        if (bills.isEmpty()) {
            modelMessageService.setModelMessage(model, "result", "No Bill Yet added");
            return "allbill";
        }
        modelMessageService.setModelMessage(model, "bills", getAllBills());

        Bill bill = billService.getBillByBillId(Integer.parseInt(billId));
        if (action.equals("details")) {
            modelMessageService.setModelMessage(model, "result", "Bill Details");
            modelMessageService.setModelMessage(model, "bill", getBillDTO(bill));
            modelMessageService.setModelMessage(model, "sales", bill.getSales());
            modelMessageService.setModelMessage(model, "totalAmountInWord", numberToWordService.convertNumberToWord(bill.getTotalAmount()));
            return "billdetails";
        }
        if (action.equals("delete")) {
            Customer customer = bill.getCustomer();
            double previousDues = customer.getPreviousDueAmount();
            customer.setPreviousDueAmount(previousDues + bill.getPaidAmount() - bill.getTotalAmount());
            customer.setCustomerPreviousBillNo(customer.getCustomerPreviousBillNo() - 1);
            customerService.addCustomer(customer);
            List<Sale> saleList = bill.getSales();
            saleList.forEach(sale -> {
                int saledItemQantity = sale.getSaleAmount();
                Item item = itemService.findItemByItemId(sale.getItem().getItemId());
                int previousQuantity = item.getItemAmount();
                item.setItemAmount(previousQuantity + saledItemQantity);
                itemService.addItem(item, request);
                itemSaleBranchRepository.deleteAllBySaleId(sale.getSaleId());
            });
            billService.deleteBill(bill.getBillId());
            if (loginController.isBranch(model)) {
                return "allbill";

            }
            return "redirect:/admin/bill/all";
        }
        modelMessageService.setModelMessage(model, "result", "This bill not Exist there");
        return "allbill";

    }

    private List<BillDTO> getBillDTOList(List<Bill> billList) {
        List<BillDTO> billDTOList = new ArrayList<>();
        billList.forEach(bill -> {
            billDTOList.add(getBillDTO(bill));
        });
        return billDTOList;
    }

    private BillDTO getBillDTO(Bill bill) {
        BillDTO billDTO = new BillDTO();
        billDTO.setBillId(bill.getBillId());
        billDTO.setCompany(bill.getCompany());
        billDTO.setCustomer(bill.getCustomer());
        billDTO.setSalesperson(bill.getSalesperson());
        billDTO.setBillNo(bill.getBillNo());
        billDTO.setBillingDate(bill.getBillingDate());
        billDTO.setPreviousDueAmount(numberToWordService.castDoubleToString(bill.getPreviousDueAmount()));
        billDTO.setNewDueAmount(numberToWordService.castDoubleToString(bill.getNewDueAmount()));
        billDTO.setPaidAmount(numberToWordService.castDoubleToString(bill.getPaidAmount()));
        billDTO.setTotalAmount(numberToWordService.castDoubleToString(bill.getTotalAmount()));
        billDTO.setTotalPayableAmount(numberToWordService.castDoubleToString(bill.getTotalPayableAmount()));
        billDTO.setTotalQuantity(bill.getTotalQuantity());
        return billDTO;
    }

    private List<Bill> getAllBills() {
        return billService.getBillList();
    }

    @GetMapping(value = {"/admin/reports", "/branch/reports"})
    public String getAllReports(Model model) {
        return "allreports";
    }

    @GetMapping(value = {"/admin/report/today", "/branch/report/today"})
    public String getTodayReport(Model model) {
        modelMessageService.setEmptyResultMessage(model);
        modelMessageService.setModelMessage(model, "heading", "Report on Sale (Today)");
        List<Bill> todayBillList = billService.getAllByBillingDate(new Date(), model);
        reportOnBills(model, todayBillList);
        return "todayortotalreport";
    }

    @GetMapping(value = {"/admin/report/total", "/branch/report/total"})
    public String getTotalSaleReport(Model model) {
        modelMessageService.setEmptyResultMessage(model);
        modelMessageService.setModelMessage(model, "heading", "Report on Sale (Total)");
        List<Bill> billList = billService.getBillList();
        reportOnBills(model, billList);
        return "todayortotalreport";
    }

    @GetMapping(value = {"/admin/report/by_customer", "/branch/report/by_customer"})
    public String salesReportByCustomer(Model model) {
        modelMessageService.setModelMessage(model, "result", "");
        return showCustomer(model);
    }

    @GetMapping(value = {"/admin/report/by_item", "/branch/report/by_item"})
    public String salesReportByBranch(Model model) {
        modelMessageService.setModelMessage(model, "result", "");
        return showItem(model);
    }

    @GetMapping(value = {"/admin/report/by_salesperson", "/branch/report/by_salesperson"})
    public String salesReportBySalesPerson(Model model) {
        modelMessageService.setEmptyResultMessage(model);
        return showSalesperson(model);
    }

    @GetMapping(value = {"/admin/report/by_branch", "/branch/report/by_branch"})
    public String searchItemByBranch(Model model) {
        modelMessageService.setEmptyResultMessage(model);
//        modelMessageService.setModelMessage(model, "item", new Item());
        return showBranch(model);
    }

    @GetMapping(value = "/admin/report/by_branch_customer")
    public String searchByBranchAndCustomer(Model model) {
        modelMessageService.setEmptyResultMessage(model);
        modelMessageService.setModelMessage(model, "branches", branchService.getAllBranch());
        return showBranchWiseCustomer(model);
    }

    @GetMapping(value = "/admin/report/by_branch_item")
    public String searchByBranchAndItem(Model model) {
        modelMessageService.setEmptyResultMessage(model);
        modelMessageService.setModelMessage(model, "branches", branchService.getAllBranch());
        return showBranchWiseItem(model);
    }

    @GetMapping(value = "/admin/report/by_branch_salesperson")
    public String searchByBranchAndSalesperson(Model model) {
        modelMessageService.setEmptyResultMessage(model);
        if (loginController.isBranch(model)) {
            User user = loginController.getAuthenticatedUser();
            Long branch_id = user.getBranch().getId();
            modelMessageService.setModelMessage(model, "branches", branchService.findBranchById(branch_id));
            return showBranchWiseSalesperson(model);
        }
        modelMessageService.setModelMessage(model, "branches", branchService.getAllBranch());
        return showBranchWiseSalesperson(model);
    }


    @GetMapping(value = {"/admin/report/by_date", "/branch/report/by_date"})
    public String salesReportByDate(Model model) {
        modelMessageService.setEmptyResultMessage(model);
        return showDate(model);
    }

    @PostMapping(value = {"/admin/report/by_customer", "/branch/report/by_customer"})
    public String getSalesReportByCustomer(@Valid @ModelAttribute Report report, Model model) {
        modelMessageService.setModelMessage(model, "heading", "Report on Sale (By Customer)");
        modelMessageService.setModelMessage(model, "nameHeading", "Customer Name: ");
        if (report.getCustomer().getId() == 0 && report.getStartingDate().equals("") && report.getEndingDate().equals("")) {
            List<Bill> billList = billService.getBillList();
            reportOnBills(model, billList);
            if (billList.isEmpty()) {
                modelMessageService.setModelMessage(model, "name", "");
                return "customerreport";
            }
            modelMessageService.setModelMessage(model, "name", "All Customer");
            return "customerreport";
        }

        if (report.getCustomer().getId() == 0 && report.getStartingDate() != "" && report.getEndingDate() != "") {
            try {
                Date startDate = stringToDateConverter(report.getStartingDate());
                Date endDate = stringToDateConverter(report.getEndingDate());
                List<Bill> billList = billService.getAllByCustomerAndTwoSelectedDate(startDate, endDate);
                reportOnBills(model, billList);
                if (billList.isEmpty()) {
                    modelMessageService.setModelMessage(model, "name", "");
                    return "customerreport";
                }
                String customerName = billList.get(0).getCustomer().getCustomerName();
                modelMessageService.setModelMessage(model, "name", "All Customer");
                return "customerreport";
            } catch (ParseException e) {
                modelMessageService.setModelMessage(model, "result", "Nothing to Show");
            }
        }
        if (report.getCustomer().getId() != 0 && report.getStartingDate().equals("") && report.getEndingDate().equals("")) {
            List<Bill> billList = billService.getBillListByCustomerId(report.getCustomer().getId(), model);
            reportOnBills(model, billList);
            if (billList.isEmpty()) {
                modelMessageService.setModelMessage(model, "name", "");
                return "customerreport";
            }
            String customerName = billList.get(0).getCustomer().getCustomerName();
            modelMessageService.setModelMessage(model, "name", customerName);
            return "customerreport";
        }

        if (report.getCustomer().getId() != 0 && report.getStartingDate() != "" && report.getEndingDate() != "") {
            try {
                Date startDate = stringToDateConverter(report.getStartingDate());
                Date endDate = stringToDateConverter(report.getEndingDate());
                List<Bill> billList = billService.getAllByCustomerIdAndTwoSelectedDate(report.getCustomer().getId(), startDate, endDate);
                reportOnBills(model, billList);
                if (billList.isEmpty()) {
                    modelMessageService.setModelMessage(model, "name", "");
                    return "customerreport";
                }
                String customerName = billList.get(0).getCustomer().getCustomerName();
                modelMessageService.setModelMessage(model, "name", customerName);
                return "customerreport";
            } catch (ParseException e) {
                modelMessageService.setModelMessage(model, "result", "Nothing to Show");
            }
        }
        return showInputErrorCustomerView(model);

    }

    @PostMapping(value = {"/admin/report/by_date", "/branch/report/by_date"})
    public String getSalesReportByDate(@Valid @ModelAttribute Report report, Model model) {
        if (report.getStartingDate() == null || report.getEndingDate() == null) {
            modelMessageService.setModelMessage(model, "result", "Can't proceed with wrong input. Please Check your fields");
            return showDate(model);
        }
        modelMessageService.setModelMessage(model, "heading", "Report on Sale (By Date)");
        try {
            Date startDate = stringToDateConverter(report.getStartingDate());
            Date endDate = stringToDateConverter(report.getEndingDate());
            List<Bill> billList = billService.getAllByTwoSelectedDate(startDate, endDate, model);
            reportOnBills(model, billList);
            return "todayortotalreport";
        } catch (ParseException e) {
            modelMessageService.setModelMessage(model, "result", "Can't proceed with wrong input");
        }
        modelMessageService.setModelMessage(model, "result", "Can't proceed with wrong input. Please Check your fields");
        return showDate(model);
    }

    @PostMapping(value = {"/admin/report/by_item", "/branch/report/by_item"})
    public String getSalesReportByItem(@Valid @ModelAttribute Report report, Model model) {
        modelMessageService.setModelMessage(model, "heading", "Report on Sale (By Item)");
        modelMessageService.setModelMessage(model, "nameHeading", "Item Name: ");
        if (report.getItem().getItemId() != 0 && report.getStartingDate().equals("") && report.getEndingDate().equals("")) {
            List<Bill> billList = billService.getBillListByItemId(report.getItem().getItemId(), model);
            modelMessageService.setModelMessage(model, "totalOfTotalAmountText", "");
            modelMessageService.setModelMessage(model, "totalOfTotalAmount", "");
            modelMessageService.setModelMessage(model, "totalQty", "");
            reportOnBills(model, billList);
            if (billList.isEmpty()) {
                modelMessageService.setModelMessage(model, "name", "");
                return "salespersonreport";
            }
            String itemName = billList.get(0).getSales().get(0).getItem().getItemName();
            modelMessageService.setModelMessage(model, "name", itemName);
            return "salespersonreport";
        }
        if (report.getItem().getItemId() == 0 && report.getStartingDate().equals("") && report.getEndingDate().equals("")) {
            List<Bill> billList = billService.getBillList();
            modelMessageService.setModelMessage(model, "totalOfTotalAmountText", "");
            modelMessageService.setModelMessage(model, "totalOfTotalAmount", "");
            modelMessageService.setModelMessage(model, "totalQty", "");
            reportOnBills(model, billList);
            if (billList.isEmpty()) {
                modelMessageService.setModelMessage(model, "name", "");
                return "salespersonreport";
            }
            String itemName = billList.get(0).getSales().get(0).getItem().getItemName();
            modelMessageService.setModelMessage(model, "name", itemName);
            return "salespersonreport";
        }
        if (report.getItem().getItemId() == 0 && report.getStartingDate() != "" && report.getEndingDate() != "") {
            try {
                Date startDate = stringToDateConverter(report.getStartingDate());
                Date endDate = stringToDateConverter(report.getEndingDate());
                List<Bill> billList = billService.getAllBySItemTwoSelectedDate(startDate, endDate);
                reportOnBills(model, billList);
                if (billList.isEmpty()) {
                    modelMessageService.setModelMessage(model, "name", "");
                    return "salespersonreport";
                }
                String itemName = billList.get(0).getSales().get(0).getItem().getItemName();
                setTotalOfTotalAmount(billList, model);
                modelMessageService.setModelMessage(model, "totalOfTotalAmountText", "Total");
                modelMessageService.setModelMessage(model, "name", itemName);
                return "salespersonreport";
            } catch (ParseException e) {
                modelMessageService.setModelMessage(model, "result", "Nothing to Show");
            }
        }
        if (report.getItem().getItemId() != 0 && report.getStartingDate() != "" && report.getEndingDate() != "") {
            try {
                Date startDate = stringToDateConverter(report.getStartingDate());
                Date endDate = stringToDateConverter(report.getEndingDate());
                List<Bill> billList = billService.getAllByItemIdIdAndTwoSelectedDate(report.getItem().getItemId(), startDate, endDate);
                reportOnBills(model, billList);
                if (billList.isEmpty()) {
                    modelMessageService.setModelMessage(model, "name", "");
                    return "salespersonreport";
                }
                String itemName = billList.get(0).getSales().get(0).getItem().getItemName();
                setTotalOfTotalAmount(billList, model);
                modelMessageService.setModelMessage(model, "totalOfTotalAmountText", "Total");
                modelMessageService.setModelMessage(model, "name", itemName);
                return "salespersonreport";
            } catch (ParseException e) {
                modelMessageService.setModelMessage(model, "result", "Nothing to Show");
            }
        }
        return showInputErrorSalespersonView(model);

    }


    @PostMapping(value = {"/admin/report/by_branch", "/branch/report/by_branch"})
    public String getSalesReportByBranch(@Valid @ModelAttribute Report report, Model model) {
        modelMessageService.setModelMessage(model, "heading", "Report on Sale (By Branch)");
        modelMessageService.setModelMessage(model, "nameHeading", "Branch Name: ");
        if (report.getBranch().getId() != 0 && report.getStartingDate().equals("") && report.getEndingDate().equals("")) {
            List<Bill> billList = billService.getBillListByBranchId(report.getBranch().getId());
            modelMessageService.setModelMessage(model, "totalOfTotalAmountText", "");
            modelMessageService.setModelMessage(model, "totalOfTotalAmount", "");
            modelMessageService.setModelMessage(model, "totalQty", "");
            reportOnBills(model, billList);
            if (billList.isEmpty()) {
                modelMessageService.setModelMessage(model, "name", "");
                return "salespersonreport";
            }
            String salespersonName = branchService.findBranchById(report.getBranch().getId()).getName();
            modelMessageService.setModelMessage(model, "name", salespersonName);
            return "salespersonreport";
        }
        if (report.getBranch().getId() == 0 && report.getStartingDate().equals("") && report.getEndingDate().equals("")) {
            List<Bill> billList = billService.getBillList();
            modelMessageService.setModelMessage(model, "totalOfTotalAmountText", "");
            modelMessageService.setModelMessage(model, "totalOfTotalAmount", "");
            modelMessageService.setModelMessage(model, "totalQty", "");
            reportOnBills(model, billList);
            if (billList.isEmpty()) {
                modelMessageService.setModelMessage(model, "name", "");
                return "salespersonreport";
            }
            String salespersonName = branchService.findBranchById(report.getBranch().getId()).getName();
            modelMessageService.setModelMessage(model, "name", salespersonName);
            return "salespersonreport";
        }
        if (report.getBranch().getId() == 0 && report.getStartingDate() != "" && report.getEndingDate() != "") {
            try {
                Date startDate = stringToDateConverter(report.getStartingDate());
                Date endDate = stringToDateConverter(report.getEndingDate());
                List<Bill> billList = billService.getAllBySalespersonTwoSelectedDate(startDate, endDate);
                reportOnBills(model, billList);
                if (billList.isEmpty()) {
                    modelMessageService.setModelMessage(model, "name", "");
                    return "salespersonreport";
                }
                String salespersonName = branchService.findBranchById(report.getBranch().getId()).getName();
                setTotalOfTotalAmount(billList, model);
                modelMessageService.setModelMessage(model, "totalOfTotalAmountText", "Total");
                modelMessageService.setModelMessage(model, "name", salespersonName);
                return "salespersonreport";
            } catch (ParseException e) {
                modelMessageService.setModelMessage(model, "result", "Nothing to Show");
            }
        }
        if (report.getBranch().getId() != 0 && report.getStartingDate() != "" && report.getEndingDate() != "") {
            try {
                Date startDate = stringToDateConverter(report.getStartingDate());
                Date endDate = stringToDateConverter(report.getEndingDate());
                List<Bill> billList = billService.getAllByBranchIdIdIdAndTwoSelectedDate(report.getBranch().getId(), startDate, endDate);
                reportOnBills(model, billList);
                if (billList.isEmpty()) {
                    modelMessageService.setModelMessage(model, "name", "");
                    return "salespersonreport";
                }
                String salespersonName = branchService.findBranchById(report.getBranch().getId()).getName();
                setTotalOfTotalAmount(billList, model);
                modelMessageService.setModelMessage(model, "totalOfTotalAmountText", "Total");
                modelMessageService.setModelMessage(model, "name", salespersonName);
                return "salespersonreport";
            } catch (ParseException e) {
                modelMessageService.setModelMessage(model, "result", "Nothing to Show");
            }
        }
        return showInputErrorSalespersonView(model);

    }

    @PostMapping(value = {"/admin/report/by_salesperson", "/branch/report/by_salesperson"})
    public String getSalesReportBySalesPerson(@Valid @ModelAttribute Report report, Model model) {
        modelMessageService.setModelMessage(model, "heading", "Report on Sale (By Salesperson)");
        modelMessageService.setModelMessage(model, "nameHeading", "Salesperson Name: ");
        if (report.getSalesperson().getId() != 0 && report.getStartingDate().equals("") && report.getEndingDate().equals("")) {
            List<Bill> billList = billService.getBillListBySalespersonId(report.getSalesperson().getId(), model);
            modelMessageService.setModelMessage(model, "totalOfTotalAmountText", "");
            modelMessageService.setModelMessage(model, "totalOfTotalAmount", "");
            modelMessageService.setModelMessage(model, "totalQty", "");
            reportOnBills(model, billList);
            if (billList.isEmpty()) {
                modelMessageService.setModelMessage(model, "name", "");
                return "salespersonreport";
            }
            String salespersonName = billList.get(0).getSalesperson().getSalespersonName();
            modelMessageService.setModelMessage(model, "name", salespersonName);
            return "salespersonreport";
        }
        if (report.getSalesperson().getId() == 0 && report.getStartingDate().equals("") && report.getEndingDate().equals("")) {
            List<Bill> billList = billService.getBillList();
            modelMessageService.setModelMessage(model, "totalOfTotalAmountText", "");
            modelMessageService.setModelMessage(model, "totalOfTotalAmount", "");
            modelMessageService.setModelMessage(model, "totalQty", "");
            reportOnBills(model, billList);
            if (billList.isEmpty()) {
                modelMessageService.setModelMessage(model, "name", "");
                return "salespersonreport";
            }
            String salespersonName = billList.get(0).getSalesperson().getSalespersonName();
            modelMessageService.setModelMessage(model, "name", salespersonName);
            return "salespersonreport";
        }
        if (report.getSalesperson().getId() == 0 && report.getStartingDate() != "" && report.getEndingDate() != "") {
            try {
                Date startDate = stringToDateConverter(report.getStartingDate());
                Date endDate = stringToDateConverter(report.getEndingDate());
                List<Bill> billList = billService.getAllBySalespersonTwoSelectedDate(startDate, endDate);
                reportOnBills(model, billList);
                if (billList.isEmpty()) {
                    modelMessageService.setModelMessage(model, "name", "");
                    return "salespersonreport";
                }
                String salespersonName = billList.get(0).getSalesperson().getSalespersonName();
                setTotalOfTotalAmount(billList, model);
                modelMessageService.setModelMessage(model, "totalOfTotalAmountText", "Total");
                modelMessageService.setModelMessage(model, "name", salespersonName);
                return "salespersonreport";
            } catch (ParseException e) {
                modelMessageService.setModelMessage(model, "result", "Nothing to Show");
            }
        }
        if (report.getSalesperson().getId() != 0 && report.getStartingDate() != "" && report.getEndingDate() != "") {
            try {
                Date startDate = stringToDateConverter(report.getStartingDate());
                Date endDate = stringToDateConverter(report.getEndingDate());
                List<Bill> billList = billService.getAllBySalespersonIdIdAndTwoSelectedDate(report.getSalesperson().getId(), startDate, endDate);
                reportOnBills(model, billList);
                if (billList.isEmpty()) {
                    modelMessageService.setModelMessage(model, "name", "");
                    return "salespersonreport";
                }
                String salespersonName = billList.get(0).getSalesperson().getSalespersonName();
                setTotalOfTotalAmount(billList, model);
                modelMessageService.setModelMessage(model, "totalOfTotalAmountText", "Total");
                modelMessageService.setModelMessage(model, "name", salespersonName);
                return "salespersonreport";
            } catch (ParseException e) {
                modelMessageService.setModelMessage(model, "result", "Nothing to Show");
            }
        }
        return showInputErrorSalespersonView(model);

    }

    private void setTotalOfTotalAmount(List<Bill> bills, Model model) {
        final double[] totalOfTotalAmount = {0};
        final int[] totalQty = {0};
        bills.forEach(bill -> {
            totalOfTotalAmount[0] += bill.getTotalAmount();
            totalQty[0] += bill.getTotalQuantity();
        });
        modelMessageService.setModelMessage(model, "totalOfTotalAmount", numberToWordService.castDoubleToString(totalOfTotalAmount[0]));
        modelMessageService.setModelMessage(model, "totalQty", totalQty[0]);
    }

    private void reportOnBills(Model model, List<Bill> bills) {
        if (!bills.isEmpty()) {
            modelMessageService.setEmptyResultMessage(model);
            modelMessageService.setModelMessage(model, "bills", getBillDTOList(bills));
            setTotalOfTotalAmount(bills, model);
        } else {
            modelMessageService.setModelMessage(model, "result", "You have no sale to report");
        }
    }

    private String showCustomer(Model model) {
        modelMessageService.setModelMessage(model, "heading", "Report on Sale (By Customer)");
        List<Customer> customers = customerService.getAllCustomer(model);
        modelMessageService.setModelMessage(model, "customers", customers);
        modelMessageService.setModelMessage(model, "report", new Report());
        return "reportbycustomer";
    }

    private String showBranchWiseCustomer(Model model) {
        modelMessageService.setModelMessage(model, "heading", "Report on Sale (By Customer)");
        modelMessageService.setModelMessage(model, "report", new Report());
        return "saleReportByBranchWiseCustomer";
    }

    private String showBranchWiseItem(Model model) {
        modelMessageService.setModelMessage(model, "heading", "Report on Sale (By Customer)");
        modelMessageService.setModelMessage(model, "report", new Report());
        return "saleReportByBranchWiseItem";
    }

    private String showBranchWiseSalesperson(Model model) {
        modelMessageService.setModelMessage(model, "heading", "Report on Sale (By Customer)");
        modelMessageService.setModelMessage(model, "report", new Report());
        return "saleReportByBranchWiseSalesperson";
    }

    private String showItem(Model model) {

        modelMessageService.setModelMessage(model, "heading", "Report on Sale (By Item)");
        User user = loginController.getAuthenticatedUser();
        List<Item> items = new ArrayList<>();
        if (user.getRole().equals("1")) {
            items = itemService.getAllItemAdmin();
        } else {
            items = itemService.getAllItem();
        }
        modelMessageService.setModelMessage(model, "items", items);
        modelMessageService.setModelMessage(model, "report", new Report());
        return "reportbyitem";
    }

    private String showSalesperson(Model model) {
        List<Salesperson> salespersons = salespersonService.getAll();
        modelMessageService.setModelMessage(model, "salespersons", salespersons);
        modelMessageService.setModelMessage(model, "heading", "Report on Sale (By Salesperson)");
        modelMessageService.setModelMessage(model, "report", new Report());
        return "reportbysalesperson";
    }

    private String showBranch(Model model) {
        List<Branch> branches = branchService.getAllBranch();
        modelMessageService.setModelMessage(model, "branches", branches);
        modelMessageService.setModelMessage(model, "heading", "Report on Sale (By Branch)");
        modelMessageService.setModelMessage(model, "report", new Report());
        return "salereportbybranch";
    }

    private String showDate(Model model) {
        modelMessageService.setModelMessage(model, "heading", "Report on Sale (By Date)");
        modelMessageService.setModelMessage(model, "report", new Report());
        return "reportbydate";
    }

    private Date stringToDateConverter(String date) throws ParseException {
        return new SimpleDateFormat("yyyy-MM-dd").parse(date);
    }

    private String showInputErrorCustomerView(Model model) {
        modelMessageService.setModelMessage(model, "result", "Can't proceed with wrong input, Please check the field");
        return showCustomer(model);
    }

    private String showInputErrorSalespersonView(Model model) {
        modelMessageService.setModelMessage(model, "result", "Can't proceed with wrong input, Please check the field");
        return showSalesperson(model);
    }

    @GetMapping(value = {"/admin/bill/allNew", "/branch/bill/allNew"})
    @ResponseBody
    public Response getAll(Model model, HttpServletResponse httpServletResponse, Pageable pageable,
                           @RequestParam(value = "export", defaultValue = "false") boolean isExport,
                           @RequestParam(value = "search", defaultValue = "") String search,
                           @RequestParam(value = "status", defaultValue = "") String status) {

        return billService.getAll(model, pageable, isExport, search, status);
    }

    @GetMapping(value = {"/admin/sale/allNew", "/branch/sale/allNew"})
    @ResponseBody
    public Response getAllSales(Model model, HttpServletResponse httpServletResponse, Pageable pageable,
                                @RequestParam(value = "export", defaultValue = "false") boolean isExport,
                                @RequestParam(value = "search", defaultValue = "") String search,
                                @RequestParam(value = "status", defaultValue = "") String status) {

        return saleService.getAll(model, pageable, isExport, search, status);
    }

    @GetMapping(value = {"/admin/bill/allNewByCustomer", "/branch/bill/allNewByCustomer"})
    @ResponseBody
    public Response getAllByCustomer(Model model, @RequestParam(value = "customerId") int customerId, HttpServletResponse httpServletResponse, Pageable pageable,
                                     @RequestParam(value = "export", defaultValue = "false") boolean isExport,
                                     @RequestParam(value = "search", defaultValue = "") String search,
                                     @RequestParam(value = "status", defaultValue = "") String status) {

        return billService.getAllByCustomer(model, customerId, pageable, isExport, search, status);
    }
}
