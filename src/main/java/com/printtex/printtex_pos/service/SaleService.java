package com.printtex.printtex_pos.service;

import com.printtex.printtex_pos.dto.BillChalanDto;
import com.printtex.printtex_pos.dto.Response;
import com.printtex.printtex_pos.model.Sale;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.ui.Model;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

public interface SaleService {
    void addSale(Sale sale, HttpServletRequest request);

    List<Sale> getAllSale();

    List<Sale> getAllSaleByItemId(int itemId);

    void deleteSale(int id, HttpServletRequest request);

    void delete(Sale sale);

    Sale getSaleBySaleId(int saleId);

    List<Sale> getDraftSales();

    void deleteDraftSales();

    HttpEntity<byte[]> getAllReportsByType(Long id, String type, Integer customerId, HttpServletResponse response, Model model);

    HttpEntity<byte[]> getBillAndChallanReport(Integer bill_id, HttpServletResponse response, Model model);

    BillChalanDto getBillAndChallanDto(Integer bill_id);

    Response getAll(Model model, Pageable pageable, boolean isExport, String search, String status);
}
