package com.printtex.printtex_pos.service;

import net.sf.jasperreports.engine.JRException;
import org.springframework.http.HttpEntity;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;

public interface UtilityService {
    HttpEntity<byte[]> getPdfReport(String jasperName, Map<String, Object> params, HttpServletResponse response, String pdfName) throws SQLException, JRException, IOException;

    ;
}
