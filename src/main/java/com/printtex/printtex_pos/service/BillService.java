package com.printtex.printtex_pos.service;

import com.printtex.printtex_pos.dto.Response;
import com.printtex.printtex_pos.model.Bill;

import java.util.Date;
import java.util.List;

import com.printtex.printtex_pos.model.Branch;
import org.springframework.data.domain.Pageable;
import org.springframework.ui.Model;

public interface BillService {

    Bill getBillByBillId(int billId);

    Bill saveBill(Bill bill);

    List<Bill> getBillList();

    List<Bill> getBillListByCustomerId(int customerId, Model model);

    List<Bill> getBillListBySalespersonId(int salespersonId, Model model);

    List<Bill> getAllByTwoSelectedDate(Date startDate, Date endDate, Model model);

    List<Bill> getAllByBillingDate(Date billingDate, Model model);

    List<Bill> getAllByCustomerIdAndTwoSelectedDate(int customerId, Date startDate, Date endDate);

    List<Bill> getAllByCustomerAndTwoSelectedDate(Date startDate, Date endDate);

    List<Bill> getAllBySalespersonIdIdAndTwoSelectedDate(int salespersonId, Date startDate, Date endDate);

    List<Bill> getAllBySalespersonTwoSelectedDate(Date startDate, Date endDate);

    void deleteBill(int billId);

    Response getAll(Model model, Pageable pageable, boolean isExport, String search, String status);

    List<Bill> getBillList(Pageable p);

    List<Bill> getBillListByCustomer(Pageable p, int customerId);

    List<Bill> getBillListByBranch(Pageable p, long branchId);


    Integer getTotalCount();

    Integer getTotalCountByCustomer(int customerId);

    Integer getTotalCountByBranch(long branchId);

    Response getAllByCustomer(Model model, int customerId, Pageable pageable, boolean isExport, String search, String status);

    List<Bill> getBillListByItemId(int id, Model model);

    List<Bill> getAllBySItemTwoSelectedDate(Date startDate, Date endDate);

    List<Bill> getAllByItemIdIdAndTwoSelectedDate(int id, Date startDate, Date endDate);

    List<Bill> getAllByBranchAndTwoSelectedDate(Date startDate, Date endDate);

    List<Bill> getBillListByBranchId(Long id);

    List<Bill> getAllByBranchIdIdIdAndTwoSelectedDate(Long id, Date startDate, Date endDate);

    List<Branch> getAllBranch();
}
