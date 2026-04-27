package com.example.monitoringapp.report.web;

import com.example.monitoringapp.report.ReportGenerationService;
import com.example.monitoringapp.report.domain.MonitoringReport;
import com.example.monitoringapp.report.repository.MonitoringReportRepository;
import com.example.monitoringapp.report.service.dto.MonitoringReportResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/reports")
public class MonitoringReportPageController {

    private final MonitoringReportRepository reportRepository;
    private final ReportGenerationService reportGenerationService;

    public MonitoringReportPageController(
            MonitoringReportRepository reportRepository,
            ReportGenerationService reportGenerationService
    ) {
        this.reportRepository = reportRepository;
        this.reportGenerationService = reportGenerationService;
    }

    @GetMapping
    public String reportList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            Model model
    ) {
        int safeSize = Math.max(1, Math.min(size, 100));
        int safePage = Math.max(0, page);
        int offset = safePage * safeSize;

        List<MonitoringReportResponse> reports = reportRepository.findAll(safeSize, offset)
                .stream()
                .map(MonitoringReportResponse::from)
                .toList();

        long totalCount = reportRepository.countAll();
        int totalPages = (int) Math.ceil((double) totalCount / safeSize);

        model.addAttribute("reports", reports);
        model.addAttribute("page", safePage);
        model.addAttribute("size", safeSize);
        model.addAttribute("totalCount", totalCount);
        model.addAttribute("totalPages", totalPages);
        return "reports/list";
    }

    @GetMapping("/{reportId}")
    public String reportDetail(@PathVariable Long reportId, Model model) {
        MonitoringReport report = reportRepository.findById(reportId);
        if (report == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Report not found: " + reportId);
        }
        model.addAttribute("report", MonitoringReportResponse.from(report));
        return "reports/detail";
    }

    @PostMapping("/generate")
    public String generate(
            @RequestParam(required = false) String targetDt,
            RedirectAttributes redirectAttributes
    ) {
        LocalDate date = (targetDt != null && !targetDt.isBlank())
                ? LocalDate.parse(targetDt)
                : LocalDate.now();

        MonitoringReport report = reportGenerationService.generate(date);
        redirectAttributes.addFlashAttribute("message", "보고서가 생성되었습니다.");
        return "redirect:/reports/" + report.getReportId();
    }
}
