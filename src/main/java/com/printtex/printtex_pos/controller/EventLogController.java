package com.printtex.printtex_pos.controller;

import com.opencsv.CSVWriter;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.printtex.printtex_pos.model.EventLog;
import com.printtex.printtex_pos.repository.EventLogRepository;
import com.printtex.printtex_pos.service.EventLogService;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;


import javax.servlet.http.HttpServletResponse;

@Controller
public class EventLogController {
    private EventLogRepository eventLogRepository;
    private EventLogService eventLogService;

    public EventLogController(EventLogRepository eventLogRepository, EventLogService eventLogService) {
        this.eventLogRepository = eventLogRepository;
        this.eventLogService = eventLogService;
    }

    @GetMapping("/admin/CSVEventLog")
    public void exportCSV(HttpServletResponse response) throws Exception {

        //set file name and content type
        String filename = "eventLog" + "-" + java.time.LocalDate.now() + ".csv";

        response.setContentType("text/csv");
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + filename + "\"");

        //create a csv writer
        StatefulBeanToCsv<EventLog> writer = new StatefulBeanToCsvBuilder<EventLog>(response.getWriter())
                .withQuotechar(CSVWriter.NO_QUOTE_CHARACTER)
                .withSeparator(CSVWriter.DEFAULT_SEPARATOR)
                .withOrderedResults(false)
                .build();

        //write all users to csv file
        writer.write(eventLogRepository.findAll());

    }

    @GetMapping("/admin/excelEventLog")
    public ResponseEntity<Resource> getFile() {
        String filename = "eventLog" + "-" + java.time.LocalDate.now() + ".xlsx";
        InputStreamResource file = new InputStreamResource(eventLogService.load());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
                .body(file);
    }

    @GetMapping("/admin/eventLogReportDownload")
    public String downloadReport() {
        return "eventLogDownloadPage";
    }
}
