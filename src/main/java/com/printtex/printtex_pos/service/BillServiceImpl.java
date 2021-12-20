package com.printtex.printtex_pos.service;

import com.printtex.printtex_pos.controller.LoginController;
import com.printtex.printtex_pos.dto.BillDto;
import com.printtex.printtex_pos.dto.Response;
import com.printtex.printtex_pos.model.Bill;
import com.printtex.printtex_pos.model.Branch;
import com.printtex.printtex_pos.model.Customer;
import com.printtex.printtex_pos.model.User;
import com.printtex.printtex_pos.repository.BillRepository;
import com.printtex.printtex_pos.util.DateUtils;
import com.printtex.printtex_pos.util.ResponseBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.ui.Model;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

@Service
public class BillServiceImpl implements BillService {

    private final BillRepository billRepository;
    private final LoginController loginController;

    public BillServiceImpl(BillRepository billRepository, LoginController loginController) {
        this.billRepository = billRepository;
        this.loginController = loginController;
    }

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Bill getBillByBillId(int billId) {
        return billRepository.findBillByBillId(billId);
    }

    @Override
    public Bill saveBill(Bill bill) {
        User user = loginController.getAuthenticatedUser();
        bill.setUserId((long) user.getId());
        bill.setBranchId(user.getBranch().getId());
        bill = billRepository.save(bill);
        return bill;
    }

    @Override
    public List<Bill> getBillList() {

        User user = loginController.getAuthenticatedUser();
        return billRepository.findAllByBranchIdOrderByBillIdDesc(user.getBranch().getId());


    }

    @Override
    public List<Bill> getBillListByCustomerId(int customerId, Model model) {
        if (loginController.isBranch(model)) {
            User user = loginController.getAuthenticatedUser();
            return billRepository.findAllBillByCustomerIdAndBranch(customerId, user.getBranch().getId());
        }
        return billRepository.findAllBillByCustomerId(customerId);
    }


    @Override
    public List<Bill> getBillListBySalespersonId(int salespersonId, Model model) {
        if (loginController.isBranch(model)) {
            User user = loginController.getAuthenticatedUser();
            return billRepository.getAllBillBySalespersonIdAndBranchId(salespersonId, user.getBranch().getId());
        }
        return billRepository.getAllBillBySalespersonId(salespersonId);
    }

    @Override
    public List<Bill> getAllByTwoSelectedDate(Date startDate, Date endDate, Model model) {
        if (loginController.isBranch(model)) {
            User user = loginController.getAuthenticatedUser();
            return billRepository.getAllByTwoSelectedDateAndBranch(startDate, endDate, user.getBranch().getId());
        }
        return billRepository.getAllByTwoSelectedDate(startDate, endDate);
    }

    @Override
    public List<Bill> getAllByBillingDate(Date billingDate, Model model) {
        if (loginController.isBranch(model)) {
            User user = loginController.getAuthenticatedUser();
            return billRepository.findAllByBillingDateAndBranchId(billingDate, user.getBranch().getId());
        }
        return billRepository.findAllByBillingDate(billingDate);
    }

    @Override
    public List<Bill> getAllByCustomerIdAndTwoSelectedDate(int customerId, Date startDate, Date endDate) {
        return billRepository.getAllByCustomerIdAndTwoSelectedDate(customerId, startDate, endDate);
    }

    @Override
    public List<Bill> getAllByCustomerAndTwoSelectedDate(Date startDate, Date endDate) {
        return billRepository.getAllByCustomerAndTwoSelectedDate(startDate, endDate);
    }

    @Override
    public List<Bill> getAllBySalespersonIdIdAndTwoSelectedDate(int salespersonId, Date startDate, Date endDate) {
        return billRepository.getAllBySalespersonIdIdAndTwoSelectedDate(salespersonId, startDate, endDate);
    }

    @Override
    public List<Bill> getAllBySalespersonTwoSelectedDate(Date startDate, Date endDate) {
        return billRepository.getAllBySalespersonAndTwoSelectedDate(startDate, endDate);
    }

    @Override
    public void deleteBill(int billId) {
        Bill bill = billRepository.findBillByBillId(billId);
        billRepository.delete(bill);
    }

    @Override
    public List<Bill> getBillList(Pageable p) {
        return billRepository.findAllByOrderByBillIdDesc(p);
    }

    @Override
    public List<Bill> getBillListByCustomer(Pageable p, int customerId) {
        return billRepository.findAllByCustomerIdOrderByBillIdDesc(p, customerId);
    }

    @Override
    public List<Bill> getBillListByBranch(Pageable p, long branchId) {
        return billRepository.findAllByBranchIdOrderByBillIdDesc(p, branchId);
    }

    @Override
    public Integer getTotalCount() {
        return billRepository.getAllCount();
    }

    @Override
    public Integer getTotalCountByCustomer(int customerId) {
        return billRepository.getTotalCountByCustomerId(customerId);
    }


    @Override
    public Integer getTotalCountByBranch(long branchId) {
        return billRepository.getTotalCountByBranchId(branchId);
    }

    @Override
    public Response getAllByCustomer(Model model, int customerId, Pageable pageable, boolean isExport, String search, String status) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Bill> criteriaQuery = criteriaBuilder.createQuery(Bill.class);
        Root<Bill> rootEntity = criteriaQuery.from(Bill.class);

        addPredicatesForCustomer(model, customerId, criteriaBuilder, criteriaQuery, rootEntity, search);

        TypedQuery<Bill> typedQuery = entityManager.createQuery(criteriaQuery);
        return getAllResponse(criteriaQuery, typedQuery, pageable, isExport);
    }

    @Override
    public List<Bill> getBillListByItemId(int itemId, Model model) {
        return billRepository.findAllBillByItemIdOrderByBillIdDesc(itemId);
    }

    @Override
    public List<Bill> getAllBySItemTwoSelectedDate(Date startDate, Date endDate) {
        String start = DateUtils.getStringDate(startDate, "yyyy-MM-dd");
        String end = DateUtils.getStringDate(endDate, "yyyy-MM-dd");
        return billRepository.getAllByItemAndTwoSelectedDate(start, end);
    }

    @Override
    public List<Bill> getAllByItemIdIdAndTwoSelectedDate(int itemId, Date startDate, Date endDate) {
        String start = DateUtils.getStringDate(startDate, "yyyy-MM-dd");
        String end = DateUtils.getStringDate(endDate, "yyyy-MM-dd");
        return billRepository.getAllByItemIdAndTwoSelectedDate(itemId, start, end);
    }

    @Override
    public List<Bill> getAllByBranchAndTwoSelectedDate(Date startDate, Date endDate) {
        return billRepository.getAllByBranchAndTwoSelectedDate(startDate, endDate);
    }

    @Override
    public List<Bill> getBillListByBranchId(Long id) {
        return billRepository.findAllByBranchIdOrderByBillNoDesc(id);
    }

    @Override
    public List<Bill> getAllByBranchIdIdIdAndTwoSelectedDate(Long branchId, Date startDate, Date endDate) {
        return billRepository.getAllByBranchIdIdAndTwoSelectedDate(branchId, startDate, endDate);
    }

    @Override
    public List<Branch> getAllBranch() {
        return null;
    }

    private void addPredicatesForCustomer(Model model, int customerId, CriteriaBuilder criteriaBuilder, CriteriaQuery<Bill> criteriaQuery, Root<Bill> rootEntity, String search) {
        List<Predicate> predicates = new ArrayList<>();
        predicates.add(criteriaBuilder.and(criteriaBuilder.isNotNull(rootEntity.<String>get("billNo"))));
        if (loginController.isBranch(model)) {
            User user = loginController.getAuthenticatedUser();
            Long branchId = user.getBranch().getId();
            predicates.add(criteriaBuilder.and(criteriaBuilder.equal(rootEntity.<Long>get("branchId"), branchId), criteriaBuilder.equal(rootEntity.<Integer>get("customer"), customerId)));
        }

        if (search != null && search.trim().length() > 0) {

            Predicate pLike = criteriaBuilder.or(
                    criteriaBuilder.like(rootEntity.<String>get("billNo"), "%" + search + "%"));
            predicates.add(pLike);
        }

        if (predicates.isEmpty()) {

            criteriaQuery.select(rootEntity).orderBy(criteriaBuilder.desc(rootEntity.get("billId")));
        } else {

            criteriaQuery.select(rootEntity).where(predicates.toArray(new Predicate[predicates.size()])).orderBy(criteriaBuilder.desc(rootEntity.get("billId")));
        }
    }

    private void addPredicates(Model model, CriteriaBuilder criteriaBuilder, CriteriaQuery<Bill> criteriaQuery, Root<Bill> rootEntity, String search) {
        List<Predicate> predicates = new ArrayList<>();
        predicates.add(criteriaBuilder.and(criteriaBuilder.isNotNull(rootEntity.<String>get("billNo"))));
        if (loginController.isBranch(model)) {
            User user = loginController.getAuthenticatedUser();
            Long branchId = user.getBranch().getId();
            predicates.add(criteriaBuilder.and(criteriaBuilder.equal(rootEntity.<Long>get("branchId"), branchId)));
        }

        if (search != null && search.trim().length() > 0) {

            Predicate pLike = criteriaBuilder.or(
                    criteriaBuilder.like(rootEntity.<String>get("billNo"), "%" + search + "%"));
            predicates.add(pLike);
        }

        if (predicates.isEmpty()) {

            criteriaQuery.select(rootEntity).orderBy(criteriaBuilder.desc(rootEntity.get("billId")));
        } else {

            criteriaQuery.select(rootEntity).where(predicates.toArray(new Predicate[predicates.size()])).orderBy(criteriaBuilder.desc(rootEntity.get("billId")));
        }
    }

    private List<BillDto> getResponseDtoList(Page<Bill> bills) {
        List<BillDto> responseDtos = new ArrayList<>();
        bills.forEach(bill -> {
            BillDto billDto = new BillDto();
            billDto.setBillId(bill.getBillId());
            billDto.setBillingDate(DateUtils.getStringDate(bill.getBillingDate(), "dd-MMM-yyyy"));
            billDto.setBillNo(bill.getBillNo());
            billDto.setCustomerName(bill.getCustomer().getCustomerName());
            billDto.setNewDueAmount(String.valueOf(bill.getNewDueAmount()));
            billDto.setPaidAmount(String.valueOf(bill.getPaidAmount()));
            billDto.setPreviousDueAmount(String.valueOf(bill.getPreviousDueAmount()));
            billDto.setTotalPayableAmount(String.valueOf(bill.getTotalPayableAmount()));
            billDto.setTotalAmount(String.valueOf(bill.getTotalAmount()));
            responseDtos.add(billDto);
        });
        return responseDtos;
    }

    @Override
    public Response getAll(Model model, Pageable pageable, boolean isExport, String search, String status) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Bill> criteriaQuery = criteriaBuilder.createQuery(Bill.class);
        Root<Bill> rootEntity = criteriaQuery.from(Bill.class);

        addPredicates(model, criteriaBuilder, criteriaQuery, rootEntity, search);

        TypedQuery<Bill> typedQuery = entityManager.createQuery(criteriaQuery);
        return getAllResponse(criteriaQuery, typedQuery, pageable, isExport);
    }

    private Response getAllResponse(CriteriaQuery<Bill> criteriaQuery, TypedQuery<Bill> typedQuery, Pageable pageable, boolean isExport) {

        long totalRows = this.getTotalRows(criteriaQuery);
        Page<Bill> sales = new PageImpl<Bill>(typedQuery.getResultList(), pageable, typedQuery.getResultList().size());
        return ResponseBuilder.getSuccessResponse(HttpStatus.OK, this.getResponseDtoList(sales), totalRows, String.format("%s list", "Bill"));
    }

    private long getTotalRows(CriteriaQuery<Bill> criteriaQuery) {
        TypedQuery<Bill> typedQuery = entityManager.createQuery(criteriaQuery);
        return typedQuery.getResultList().size();
    }
}
