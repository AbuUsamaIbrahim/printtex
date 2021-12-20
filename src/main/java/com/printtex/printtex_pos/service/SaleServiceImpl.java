package com.printtex.printtex_pos.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.printtex.printtex_pos.controller.LoginController;
import com.printtex.printtex_pos.dto.BillChalanDto;
import com.printtex.printtex_pos.dto.Response;
import com.printtex.printtex_pos.dto.SaleDto;
import com.printtex.printtex_pos.model.*;
import com.printtex.printtex_pos.repository.ItemSaleBranchRepository;
import com.printtex.printtex_pos.repository.SaleRepository;
import com.printtex.printtex_pos.util.DateUtils;
import com.printtex.printtex_pos.util.ResponseBuilder;
import net.sf.jasperreports.engine.JRException;
import org.apache.commons.io.FileUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

@Service
public class SaleServiceImpl implements SaleService {
    private final SaleRepository saleRepository;
    private final LoginController loginController;
    private final ItemSaleBranchRepository itemSaleBranchRepository;
    private final UtilityService utilityService;
    private final ItemService itemService;
    private final BranchService branchService;
    private final EventLogService eventLogService;
    private final BillService billService;
    private final NumberToWordService numberToWordService;
    private final SalespersonService salespersonService;

    public SaleServiceImpl(SaleRepository saleRepository, LoginController loginController, ItemSaleBranchRepository itemSaleBranchRepository, UtilityService utilityService, ItemService itemService, BranchService branchService, EventLogService eventLogService, BillService billService, NumberToWordService numberToWordService, SalespersonService salespersonService) {
        this.saleRepository = saleRepository;
        this.loginController = loginController;
        this.itemSaleBranchRepository = itemSaleBranchRepository;
        this.utilityService = utilityService;
        this.itemService = itemService;
        this.branchService = branchService;
        this.eventLogService = eventLogService;
        this.billService = billService;
        this.numberToWordService = numberToWordService;
        this.salespersonService = salespersonService;
    }

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void addSale(Sale sale, HttpServletRequest request) {
        ObjectMapper mapper = new ObjectMapper();
        User user = loginController.getAuthenticatedUser();
        sale.setUserId((long) user.getId());
        sale.setBranchId(user.getBranch().getId());
        sale = saleRepository.save(sale);
        if (sale.getSaleStatus().equals("billed") && sale.getBill() != null) {
            ItemSaleBranch itemSaleBranch = new ItemSaleBranch();
            itemSaleBranch.setBranchId(sale.getBranchId());
            itemSaleBranch.setSaleId(sale.getSaleId());
            itemSaleBranch.setItemId(sale.getItem().getItemId());
            itemSaleBranch.setSalesAmount(sale.getSaleAmount());
            itemSaleBranchRepository.save(itemSaleBranch);
        }
        try {
            String newJson = mapper.writeValueAsString(sale);
            eventLogService.saveInsertLog(newJson, Sale.class, String.valueOf(sale.getSaleId()), request);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

    }

    @Override
    public List<Sale> getAllSale() {
        return saleRepository.findAll();
    }

    @Override
    public List<Sale> getAllSaleByItemId(int itemId) {
        return saleRepository.getAllSaleByItemId(itemId);
    }

    @Override
    public void deleteSale(int id, HttpServletRequest request) {
        Sale sale = saleRepository.findSaleBySaleId(id);
        ObjectMapper mapper = new ObjectMapper();
        String oldJson = null;
        try {
            oldJson = mapper.writeValueAsString(sale);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        saleRepository.delete(sale);
        eventLogService.saveDeleteLog(oldJson, Sale.class, String.valueOf(sale.getSaleId()), request);
    }

    @Override
    public void delete(Sale sale) {
        saleRepository.delete(sale);
    }

    @Override
    public Sale getSaleBySaleId(int saleId) {
        return saleRepository.findSaleBySaleId(saleId);
    }

    @Override
    public List<Sale> getDraftSales() {
        return saleRepository.getAllDraftSales();
    }

    @Override
    public void deleteDraftSales() {
        saleRepository.deleteDraftSales();
    }

    @Override
    @Transactional
    public HttpEntity<byte[]> getAllReportsByType(Long id, String type, Integer customerId, HttpServletResponse response, Model model) {
        Map<String, Object> reportParams = new HashMap<>();
        String jasperName = "";
        if (type.equals("stockInfo")) {
            if (id.equals(Long.valueOf("0"))) {
                User user = loginController.getAuthenticatedUser();
                id = user.getBranch().getId();
            }
            String branchName = branchService.findBranchById(id).getName();
            jasperName = "stock_info";
            reportParams.put("branch_id", id);
            reportParams.put("branch_name", branchName);

        }

        if (type.equals("stockInfoAll")) {
            jasperName = "stock_info";
            if (id == 0) {
                reportParams.put("branch_id", null);
                reportParams.put("branch_name", "All Branch");
            }

        }

        if (type.equals("saleByBranchWiseCustomer")) {
            String branchName = branchService.findBranchById(id).getName();
            jasperName = "sales_report_by_branch";
            reportParams.put("branch_id", id);
            reportParams.put("branch_name", branchName);
            if (customerId == 0) {
                reportParams.put("customer_id", null);
            } else {
                reportParams.put("customer_id", customerId);
            }

        }

        if (type.equals("saleByBranchWiseItem")) {
            String branchName = branchService.findBranchById(id).getName();
            jasperName = "sales_report_by_item";
            reportParams.put("branch_id", id);
            reportParams.put("branch_name", branchName);
            if (customerId == 0) {
                reportParams.put("item_name", "All Item");
                reportParams.put("item_id", null);
            } else {
                String itemName = itemService.findItemByItemId(customerId).getItemName();
                reportParams.put("item_id", customerId);
                reportParams.put("item_name", itemName);
            }

        }

        if (type.equals("saleByBranchWiseSalesPerson")) {
            String branchName = branchService.findBranchById(id).getName();
            jasperName = "sales_report_by_salesperson";
            reportParams.put("branch_id", id);
            reportParams.put("branch_name", branchName);
            if (customerId == 0) {
                reportParams.put("item_name", "All Salesperson");
                reportParams.put("item_id", null);
            } else {
                String itemName = salespersonService.findSalespersonById(customerId).getSalespersonName();
                reportParams.put("item_id", customerId);
                reportParams.put("item_name", itemName);
            }

        }

        if (type.equals("saleByItemFromBranch")) {
            Long branchId = null;
            if (loginController.isBranch(model)) {
                User user = loginController.getAuthenticatedUser();
                branchId = user.getBranch().getId();
            }
            String itemName = itemService.findItemByItemId(id.intValue()).getItemName();
            jasperName = "sales_report_by_item";
            reportParams.put("item_id", id.intValue());
            reportParams.put("item_name", itemName);
            if (branchId != null) {
                reportParams.put("branch_id", branchId);
            }
        }

        if (type.equals("saleByItem")) {
            Long branchId = null;
            if (loginController.isBranch(model)) {
                User user = loginController.getAuthenticatedUser();
                branchId = user.getBranch().getId();
            }
            if (customerId == 0) {
                jasperName = "sales_report_by_item _all";
                reportParams.put("item_name", "All Item");
            } else {
                String itemName = itemService.findItemByItemId(customerId).getItemName();
                jasperName = "sales_report_by_item";
                reportParams.put("item_id", id);
                reportParams.put("item_name", itemName);
                if (branchId != null) {
                    reportParams.put("branch_id", branchId);
                }
            }

        }
        if (type.equals("saleByBranch")) {
            if (loginController.isBranch(model)) {
                User user = loginController.getAuthenticatedUser();
                id = user.getBranch().getId();
            }
            String branchName = branchService.findBranchById(id).getName();
            jasperName = "sales_report_by_branch";
            reportParams.put("branch_id", id);
            reportParams.put("branch_name", branchName);
        }

        if (type.equals("branchInfoAll")) {
            jasperName = "sales_report_by_branch";
            reportParams.put("branch_id", null);
            reportParams.put("branch_name", "All Branch");
        }

        if (type.equals("totalSale")) {
            jasperName = "sales_report_total";
        }

        try {
            return (HttpEntity<byte[]>) utilityService.getPdfReport(jasperName, reportParams, response, type + "_of" + id);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (JRException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    @Transactional
    public HttpEntity<byte[]> getBillAndChallanReport(Integer bill_id, HttpServletResponse response, Model model) {
        Map<String, Object> reportParams = new HashMap<>();
        String jasperName = "challan_and_bill";
        Double amountInWords = billService.getBillByBillId(bill_id).getTotalAmount();
        reportParams.put("amount_in_words", numberToWordService.convertNumberToWord(amountInWords));
        reportParams.put("branch_id", bill_id);
        reportParams.put("company_name_image", billService.getBillByBillId(bill_id).getCompany().getCompanyNameLogoUrl());
        reportParams.put("logo_image", billService.getBillByBillId(bill_id).getCompany().getCompanyLogoUrl());
        try {
            return (HttpEntity<byte[]>) utilityService.getPdfReport(jasperName, reportParams, response, jasperName + "_of_" + new Date());
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (JRException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public BillChalanDto getBillAndChallanDto(Integer bill_id) {
        try {
            Bill bill = billService.getBillByBillId(bill_id);
            BillChalanDto dto = new BillChalanDto();
            dto.setBillNo(bill.getBillNo());
            dto.setCollectionDate(DateUtils.getStringDate(bill.getBillingDate(), "dd/MM/yyyy"));
            dto.setPreviousCollectionDate(bill.getPaymentDate());
            Branch branch = branchService.findBranchById(bill.getBranchId());
            dto.setCompanyAddress(branch.getAddress());
            dto.setBranchName(branch.getName());
            File fileLogo = new File(bill.getCompany().getCompanyLogoUrl());
            byte[] fileLogoContent = FileUtils.readFileToByteArray(fileLogo);
            String encodedLogoString = Base64.getEncoder().encodeToString(fileLogoContent);
            dto.setCompanyLogo(encodedLogoString);
            dto.setFooterBranchAddress(bill.getCompany().getCompanyAddress());
            dto.setCompanyName(bill.getCompany().getCompanyName());
            File fileName = new File(bill.getCompany().getCompanyNameLogoUrl());
            byte[] fileNameContent = FileUtils.readFileToByteArray(fileName);
            String encodedNameString = Base64.getEncoder().encodeToString(fileNameContent);
            dto.setCompanyLogoName(encodedNameString);
            dto.setCurrentBill(numberToWordService.addCommasToNumber(bill.getTotalAmount()));
            dto.setPreviousDue(numberToWordService.addCommasToNumber(bill.getPreviousDueAmount()));
            dto.setCustomerName(bill.getCustomer().getCustomerName());
            dto.setCustomerAddress(bill.getCustomer().getAddress());
            dto.setCustomerPhone(bill.getCustomer().getMobileNo());
            dto.setTotalPayable(numberToWordService.addCommasToNumber(bill.getTotalPayableAmount()));
            dto.setCurrentBillCollection(numberToWordService.addCommasToNumber(bill.getPaidAmount()));
            dto.setTotalDue(numberToWordService.addCommasToNumber(bill.getNewDueAmount()));
            dto.setTotalQuantity(bill.getTotalQuantity() + " kgs");
            dto.setTotalInWord(numberToWordService.convertNumberToWord(bill.getTotalAmount()));
            List<SaleDto> saleList = new ArrayList<>();
            List<Sale> sales = saleRepository.findAllByBillAndSaleStatus(bill, "billed");
            sales.forEach(sale -> {
                SaleDto saleDto = new SaleDto();
                saleDto.setGoods(sale.getItem().getItemName());
                saleDto.setQuantity(sale.getSaleAmount() + " kgs / " + sale.getSaleDrumNo() + " " + sale.getDrumOrCartoon());
                saleDto.setQuantityChallan(sale.getSaleAmount() + " kgs");
                saleDto.setUnitPrice(numberToWordService.addCommasToNumber(sale.getSaleUnitPrice()));
                saleDto.setTotalPrice(numberToWordService.addCommasToNumber(sale.getTotalSaleAmount()));
                saleList.add(saleDto);
            });
            dto.setSaleList(saleList);
            return dto;
        } catch (Exception e) {
            return new BillChalanDto();
        }
    }

    private void addPredicates(Model model, CriteriaBuilder criteriaBuilder, CriteriaQuery<Sale> criteriaQuery, Root<Sale> rootEntity, String search) {
        List<Predicate> predicates = new ArrayList<>();
        predicates.add(criteriaBuilder.and(criteriaBuilder.isNotNull(rootEntity.<String>get("saleId"))));
        if (loginController.isBranch(model)) {
            User user = loginController.getAuthenticatedUser();
            Long branchId = user.getBranch().getId();
            predicates.add(criteriaBuilder.and(criteriaBuilder.equal(rootEntity.<Long>get("branchId"), branchId)));
        }
        predicates.add(criteriaBuilder.and(criteriaBuilder.equal(rootEntity.<String>get("saleStatus"), "billed")));
        if (search != null && search.trim().length() > 0) {

            Predicate pLike = criteriaBuilder.or(
                    criteriaBuilder.like(rootEntity.<String>get("drumOrCartoon"), "%" + search + "%"));
            predicates.add(pLike);
        }

        if (predicates.isEmpty()) {

            criteriaQuery.select(rootEntity).orderBy(criteriaBuilder.desc(rootEntity.get("saleId")));
        } else {

            criteriaQuery.select(rootEntity).where(predicates.toArray(new Predicate[predicates.size()])).orderBy(criteriaBuilder.desc(rootEntity.get("billId")));
        }
    }

    @Override
    public Response getAll(Model model, Pageable pageable, boolean isExport, String search, String status) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Sale> criteriaQuery = criteriaBuilder.createQuery(Sale.class);
        Root<Sale> rootEntity = criteriaQuery.from(Sale.class);

        addPredicates(model, criteriaBuilder, criteriaQuery, rootEntity, search);

        TypedQuery<Sale> typedQuery = entityManager.createQuery(criteriaQuery);
        return getAllResponse(criteriaQuery, typedQuery, pageable, isExport);
    }

    private Response getAllResponse(CriteriaQuery<Sale> criteriaQuery, TypedQuery<Sale> typedQuery, Pageable pageable, boolean isExport) {

        long totalRows = this.getTotalRows(criteriaQuery);
        Page<Sale> sales = new PageImpl<Sale>(typedQuery.getResultList(), pageable, typedQuery.getResultList().size());
//        return ResponseBuilder.getSuccessResponse(HttpStatus.OK,this.getResponseDtoList(sales), totalRows, String.format("%s list", "Bill"));
        return null;
    }

    private long getTotalRows(CriteriaQuery<Sale> criteriaQuery) {
        TypedQuery<Sale> typedQuery = entityManager.createQuery(criteriaQuery);
        return typedQuery.getResultList().size();
    }


}
