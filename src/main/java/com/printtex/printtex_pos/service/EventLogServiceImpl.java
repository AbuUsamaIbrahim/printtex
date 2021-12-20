package com.printtex.printtex_pos.service;

import com.google.common.base.CaseFormat;
import com.printtex.printtex_pos.helper.ExcelHelper;
import com.printtex.printtex_pos.model.EventLog;
import com.printtex.printtex_pos.repository.EventLogRepository;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayInputStream;
import java.util.List;

@Service
public class EventLogServiceImpl implements EventLogService {
    private static final String[] IP_HEADER_CANDIDATES = {
            "X-Forwarded-For",
            "Proxy-Client-IP",
            "WL-Proxy-Client-IP",
            "HTTP_X_FORWARDED_FOR",
            "HTTP_X_FORWARDED",
            "HTTP_X_CLUSTER_CLIENT_IP",
            "HTTP_CLIENT_IP",
            "HTTP_FORWARDED_FOR",
            "HTTP_FORWARDED",
            "HTTP_VIA",
            "REMOTE_ADDR"};

    private final EventLogRepository eventLogRepository;

    public EventLogServiceImpl(EventLogRepository eventLogRepository) {
        this.eventLogRepository = eventLogRepository;
    }


    @Override
    public void saveInsertLog(String newJson, Class entityName, String rowId, HttpServletRequest request) {
        saveEventLog("Insert", newJson, null, entityName.getSimpleName(), rowId, request);
    }

    @Override
    public void saveUpdateLog(String newJson, String oldJson, Class entityName, String rowId, HttpServletRequest request) {
        saveEventLog("Update", newJson, oldJson, entityName.getSimpleName(), rowId, request);
    }

    @Override
    public void saveDeleteLog(String oldJson, Class entityName, String rowId, HttpServletRequest request) {
        saveEventLog("Delete", null, oldJson, entityName.getSimpleName(), rowId, request);
    }

    private void saveEventLog(String operation, String newJson, String oldJson, String tableName, String rowId, HttpServletRequest request) {
        EventLog eventLog = new EventLog();
        eventLog.setOperation(operation);
        eventLog.setPreviousValue(oldJson);
        eventLog.setPresentValue(newJson);
        eventLog.setTableName(CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, tableName));
        eventLog.setRowId(rowId);
        eventLog.setIpAddress(getClientIpAddress(request));
        eventLogRepository.save(eventLog);
    }

    public ByteArrayInputStream load() {
        List<EventLog> eventLogs = eventLogRepository.findAll();
        return ExcelHelper.eventLogToExcel(eventLogs);
    }


    private String getClientIpAddress(HttpServletRequest request) {
        for (String header : IP_HEADER_CANDIDATES) {
            String ip = request.getHeader(header);
            if (ip != null && ip.length() != 0 && !"unknown".equalsIgnoreCase(ip)) {
                return ip;
            }
        }
        return request.getRemoteAddr();
    }
}
