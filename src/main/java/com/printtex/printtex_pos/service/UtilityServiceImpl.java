package com.printtex.printtex_pos.service;

import com.printtex.printtex_pos.util.DateUtils;
import com.zaxxer.hikari.HikariDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Date;
import java.util.Map;

@Service
public class UtilityServiceImpl implements UtilityService {
    private static final String REPORT_DIR = "REPORT_DIR";
    private static final String IMAGE_DIR = "IMAGE_DIR";
    private static final String SUBREPORT_DIR = "SUBREPORT_DIR";
    @Value("${spring.datasource.url}")
    private String url;
    @Value("${spring.datasource.username}")
    private String username;
    @Value("${spring.datasource.password}")
    private String password;
    @Value("${spring.datasource.driver-class-name}")
    private String driverName;
    @Value("${report.path}")
    private String reportPath;
    private static final Logger logger = LogManager.getLogger(UtilityServiceImpl.class.getName());

    @Override
    public HttpEntity<byte[]> getPdfReport(String jasperName, Map<String, Object> reportParams, HttpServletResponse response, String pdfName) throws SQLException, JRException, IOException {
        reportParams.put(SUBREPORT_DIR, reportPath + File.separator);
        reportParams.put(REPORT_DIR, reportPath + File.separator);
        reportParams.put(IMAGE_DIR, reportPath + File.separator);
        try {
            Class.forName(driverName);
            Connection con = DriverManager.getConnection(url, username, password);
            JasperPrint print = JasperFillManager.fillReport(reportPath + File.separator + jasperName + ".jasper", reportParams/*, dataSource.getConnection()*/, con);
            byte[] pdfBytes = JasperExportManager.exportReportToPdf(print);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            if (pdfName == null || pdfName.trim().equals("")) {
                response.setHeader("Content-Disposition", "inline; filename=" + jasperName + "_" + DateUtils.getStringDate(new Date(), "dd-MM-yyyy") + ".pdf");
            } else {
                response.setHeader("Content-Disposition", "inline; filename=" + pdfName + "_" + DateUtils.getStringDate(new Date(), "dd-MM-yyyy") + ".pdf");
            }
            if (!con.isClosed()) {
                con.close();
            }
            return new HttpEntity<byte[]>(pdfBytes, headers);
        } catch (ClassNotFoundException e) {
            logger.error(e.getMessage());
            return null;
        }
    }
}
