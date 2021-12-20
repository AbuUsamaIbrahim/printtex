package com.printtex.printtex_pos.controller;

import com.printtex.printtex_pos.dto.BillChalanDto;
import com.printtex.printtex_pos.service.SaleService;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
public class FileController {
    private final SaleService saleService;

    public FileController(SaleService saleService) {
        this.saleService = saleService;
    }

    @GetMapping(value = {"/admin/download", "/branch/download"})
    @ResponseBody
    public HttpEntity<byte[]> download(@RequestParam(name = "branch_id", required = false) Long branch_id, @RequestParam(name = "report_type") String report_type, @RequestParam(name = "customer_id") Integer customerId, Model model, HttpServletResponse response, HttpServletRequest request) {
        return saleService.getAllReportsByType(branch_id, report_type, customerId, response, model);
    }

    @GetMapping(value = {"/admin/downloadBillChallan", "/branch/downloadBillChallan"})
    @ResponseBody
    public HttpEntity<byte[]> download(@RequestParam(name = "bill_id", required = false) Integer bill_id, Model model, HttpServletResponse response, HttpServletRequest request) {
        return saleService.getBillAndChallanReport(bill_id, response, model);
    }
//@GetMapping(value = {"/admin/downloadBillChallan","/branch/downloadBillChallan"})
//@ResponseBody
//public BillChalanDto download(@RequestParam(name = "bill_id")Integer bill_id, Model model, HttpServletResponse response, HttpServletRequest request){
//    return saleService.getBillAndChallanDto(bill_id);
//}
//    @GetMapping(UrlConstants.FileManagement.DOWNLOAD_FILE_BY_PARAMS)
//    public HttpEntity<byte[]> downloadByParams(@RequestParam(name = "sales_id")Long sales_id, @RequestParam(name = "bank")String bank,@RequestParam(name = "branch")String branch,@RequestParam(name = "address")String address,@RequestParam(name = "account")String account, HttpServletResponse response, HttpServletRequest request){
//        return saleService.getReportsByParams(sales_id,bank,branch,address,account,response);
//    }
}
