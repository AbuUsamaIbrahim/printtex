package com.printtex.printtex_pos.service;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayInputStream;

public interface EventLogService {

    void saveInsertLog(String newJson, Class entityName, String rowId, HttpServletRequest request);

    void saveUpdateLog(String newJson, String oldJson, Class entityName, String rowId, HttpServletRequest request);

    void saveDeleteLog(String oldJson, Class entityName, String rowId, HttpServletRequest request);

    ByteArrayInputStream load();
}
