package com.example.monitoringapp.report.rest;

import com.example.monitoringapp.report.ReportGenerationService;
import com.example.monitoringapp.report.domain.MonitoringReport;
import com.example.monitoringapp.report.repository.MonitoringReportRepository;
import com.example.monitoringapp.report.service.dto.MonitoringReportPageResponse;
import com.example.monitoringapp.report.service.dto.MonitoringReportResponse;
import com.example.monitoringapp.report.service.dto.ReportGenerateRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/monitoring-app/reports")
public class MonitoringReportRestController {

    private final MonitoringReportRepository reportRepository;
    private final ReportGenerationService reportGenerationService;

    public MonitoringReportRestController(
            MonitoringReportRepository reportRepository,
            ReportGenerationService reportGenerationService
    ) {
        this.reportRepository = reportRepository;
        this.reportGenerationService = reportGenerationService;
    }

    @GetMapping
    public MonitoringReportPageResponse getReports(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        int safeSize = Math.max(1, Math.min(size, 100));
        int safePage = Math.max(0, page);
        int offset = safePage * safeSize;

        List<MonitoringReportResponse> contents = reportRepository.findAll(safeSize, offset)
                .stream()
                .map(MonitoringReportResponse::from)
                .toList();

        MonitoringReportPageResponse response = new MonitoringReportPageResponse();
        response.setPage(safePage);
        response.setSize(safeSize);
        response.setTotalCount(reportRepository.countAll());
        response.setContents(contents);
        return response;
    }

    @GetMapping("/latest")
    public MonitoringReportResponse getLatest() {
        MonitoringReport report = reportRepository.findLatest();
        if (report == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No reports found");
        }
        return MonitoringReportResponse.from(report);
    }

    @GetMapping("/{reportId}")
    public MonitoringReportResponse getReport(@PathVariable Long reportId) {
        MonitoringReport report = reportRepository.findById(reportId);
        if (report == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Report not found: " + reportId);
        }
        return MonitoringReportResponse.from(report);
    }

    @PostMapping("/generate")
    public MonitoringReportResponse generate(@RequestBody(required = false) ReportGenerateRequest request) {
        LocalDate targetDt = (request != null && request.getTargetDt() != null)
                ? request.getTargetDt()
                : LocalDate.now();
        MonitoringReport report = reportGenerationService.generate(targetDt);
        return MonitoringReportResponse.from(report);
    }
}
