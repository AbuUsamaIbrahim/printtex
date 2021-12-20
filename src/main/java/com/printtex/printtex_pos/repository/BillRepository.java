package com.printtex.printtex_pos.repository;

import com.printtex.printtex_pos.model.Bill;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface BillRepository extends PagingAndSortingRepository<Bill, Integer> {

    Bill findBillByBillId(int billId);

    @Query("SELECT b FROM Bill b WHERE b.customer.id = :customerId")
    List<Bill> findAllBillByCustomerId(@Param("customerId") int customerId);

    @Query("SELECT b FROM Bill b WHERE b.customer.id = :customerId and b.branchId= :branchId")
    List<Bill> findAllBillByCustomerIdAndBranch(@Param("customerId") int customerId, @Param("branchId") Long branchId);

    @Query("SELECT COUNT(b) FROM Bill b")
    Integer getAllCount();

    @Query(value = "select b from Bill b where b.salesperson.id = :salespersonId and b.branchId=:branchId")
    List<Bill> getAllBillBySalespersonIdAndBranchId(@Param("salespersonId") int salespersonId, @Param("branchId") Long branchId);

    @Query(value = "select b from Bill b where b.salesperson.id = :salespersonId")
    List<Bill> getAllBillBySalespersonId(@Param("salespersonId") int salespersonId);

    @Query(value = "SELECT b FROM Bill b WHERE b.billingDate between :startDate and :endDate")
    List<Bill> getAllByTwoSelectedDate(@Param("startDate") Date startDate, @Param("endDate") Date endDate);

    @Query(value = "SELECT b FROM Bill b WHERE b.branchId=:branchId and b.billingDate between :startDate and :endDate")
    List<Bill> getAllByTwoSelectedDateAndBranch(@Param("startDate") Date startDate, @Param("endDate") Date endDate, @Param("branchId") Long branchId);

    @Query(value = "SELECT b FROM Bill b WHERE b.customer.id = :customerId AND b.billingDate between :startDate and :endDate")
    List<Bill> getAllByCustomerIdAndTwoSelectedDate(@Param("customerId") int customerId, @Param("startDate") Date startDate, @Param("endDate") Date endDate);

    @Query(value = "SELECT b FROM Bill b WHERE b.billingDate between :startDate and :endDate")
    List<Bill> getAllByCustomerAndTwoSelectedDate(@Param("startDate") Date startDate, @Param("endDate") Date endDate);

    @Query(value = "SELECT b FROM Bill b WHERE b.salesperson.id = :salespersonId AND b.billingDate between :startDate and :endDate")
    List<Bill> getAllBySalespersonIdIdAndTwoSelectedDate(@Param("salespersonId") int salespersonId, @Param("startDate") Date startDate, @Param("endDate") Date endDate);

    @Query(value = "SELECT b FROM Bill b WHERE  b.billingDate between :startDate and :endDate")
    List<Bill> getAllBySalespersonAndTwoSelectedDate(@Param("startDate") Date startDate, @Param("endDate") Date endDate);

    List<Bill> findAllByBillingDate(@Param("billingDate") Date billingDate);

    List<Bill> findAllByBillingDateAndBranchId(@Param("billingDate") Date billingDate, Long branchId);


    List<Bill> findAllByOrderByBillIdDesc();

    List<Bill> findAllByOrderByBillIdDesc(Pageable p);

    @Query("SELECT b FROM Bill b WHERE b.customer.id = :customerId")
    List<Bill> findAllByCustomerIdOrderByBillIdDesc(Pageable p, @Param("customerId") int customerId);

    @Query("SELECT b FROM Bill b WHERE b.branchId = :branchId")
    List<Bill> findAllByBranchIdOrderByBillIdDesc(Pageable p, @Param("branchId") long branchId);

    @Query("SELECT COUNT(b) FROM Bill b WHERE b.customer.id = :customerId")
    Integer getTotalCountByCustomerId(@Param("customerId") int customerId);

    @Query("SELECT COUNT(b) FROM Bill b WHERE b.branchId = :branchId")
    Integer getTotalCountByBranchId(@Param("branchId") long branchId);

    List<Bill> findAllByBranchIdOrderByBillIdDesc(Long id);

    @Query(value = "select * from bill b left join sale s on s.bill_id=b.bill_id where s.item_Id = :itemId", nativeQuery = true)
    List<Bill> findAllBillByItemIdOrderByBillIdDesc(@Param("itemId") int itemId);

    @Query(value = "SELECT *  FROM bill b left join sale s on s.bill_id=b.bill_id where  b.billing_date between :startDate and :endDate", nativeQuery = true)
    List<Bill> getAllByItemAndTwoSelectedDate(@Param("startDate") String startDate, @Param("endDate") String endDate);

    @Query(value = "SELECT *  FROM bill b left join sale s on s.bill_id=b.bill_id where s.item_Id = :itemId AND b.billing_date between :startDate and :endDate", nativeQuery = true)
    List<Bill> getAllByItemIdAndTwoSelectedDate(@Param("itemId") int itemId, @Param("startDate") String startDate, @Param("endDate") String endDate);

    @Query(value = "SELECT b FROM Bill b WHERE b.billingDate between :startDate and :endDate")
    List<Bill> getAllByBranchAndTwoSelectedDate(Date startDate, Date endDate);

    List<Bill> findAllByBranchIdOrderByBillNoDesc(Long id);

    @Query(value = "SELECT b FROM Bill b WHERE b.branchId = :branchId AND b.billingDate between :startDate and :endDate")
    List<Bill> getAllByBranchIdIdAndTwoSelectedDate(@Param("branchId") Long branchId, @Param("startDate") Date startDate, @Param("endDate") Date endDate);
}