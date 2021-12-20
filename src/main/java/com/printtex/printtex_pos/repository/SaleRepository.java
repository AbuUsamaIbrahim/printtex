package com.printtex.printtex_pos.repository;

import com.printtex.printtex_pos.model.Bill;
import com.printtex.printtex_pos.model.Sale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface SaleRepository extends JpaRepository<Sale, Integer> {
    @Query(value = "select s from Sale s where s.item.itemId = :itemId")
    List<Sale> getAllSaleByItemId(@Param("itemId") int itemId);

    Sale findSaleBySaleId(int saleId);

    @Query(value = "SELECT s FROM Sale s WHERE s.saleStatus= 'draft'")
    List<Sale> getAllDraftSales();

    List<Sale> findAllByBillAndSaleStatus(Bill bill, String saleStatus);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM Sale s WHERE s.saleStatus= 'draft'")
    void deleteDraftSales();

}
