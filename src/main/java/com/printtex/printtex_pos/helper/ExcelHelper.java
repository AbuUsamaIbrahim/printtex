package com.printtex.printtex_pos.helper;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import com.printtex.printtex_pos.model.EventLog;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelHelper {
    public static String TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    static String[] HEADERs = {"Id", "Table Name", "Previous Value", "Present Value", "Row Id", "User Id", "Operation", "IP Address", "Event Date"};
    static String SHEET = "Tutorials";

    public static ByteArrayInputStream eventLogToExcel(List<EventLog> eventLogs) {

        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream();) {
            Sheet sheet = workbook.createSheet(SHEET);

            // Header
            Row headerRow = sheet.createRow(0);

            for (int col = 0; col < HEADERs.length; col++) {
                Cell cell = headerRow.createCell(col);
                cell.setCellValue(HEADERs[col]);
            }

            int rowIdx = 1;
            for (EventLog eventLog : eventLogs) {
                Row row = sheet.createRow(rowIdx++);

                row.createCell(0).setCellValue(eventLog.getId());
                row.createCell(1).setCellValue(eventLog.getTableName());
                row.createCell(2).setCellValue(eventLog.getPreviousValue());
                row.createCell(4).setCellValue(eventLog.getPresentValue());
                row.createCell(5).setCellValue(eventLog.getRowId());
//                row.createCell(6).setCellValue(eventLog.getUserId());
                row.createCell(7).setCellValue(eventLog.getOperation());
                row.createCell(8).setCellValue(eventLog.getIpAddress());
                row.createCell(9).setCellValue(eventLog.getEventDate());
            }

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException("fail to import data to Excel file: " + e.getMessage());
        }
    }
}
