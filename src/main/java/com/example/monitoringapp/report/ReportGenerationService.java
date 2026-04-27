package com.example.monitoringapp.report;

import com.example.monitoringapp.report.domain.MonitoringReport;
import com.example.monitoringapp.report.domain.ReportAnalysisContext;
import com.example.monitoringapp.report.repository.MonitoringReportRepository;
import com.example.monitoringapp.report.step.ReportAiAnalyzer;
import com.example.monitoringapp.report.step.ReportDataCollector;
import com.example.monitoringapp.report.step.ReportPromptContextBuilder;
import com.example.monitoringapp.report.step.ReportSummaryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class ReportGenerationService {

    private static final Logger log = LoggerFactory.getLogger(ReportGenerationService.class);

    private final MonitoringReportRepository reportRepository;
    private final ReportDataCollector dataCollector;
    private final ReportSummaryBuilder summaryBuilder;
    private final ReportPromptContextBuilder promptContextBuilder;
    private final ReportAiAnalyzer aiAnalyzer;

    public ReportGenerationService(
            MonitoringReportRepository reportRepository,
            ReportDataCollector dataCollector,
            ReportSummaryBuilder summaryBuilder,
            ReportPromptContextBuilder promptContextBuilder,
            ReportAiAnalyzer aiAnalyzer
    ) {
        this.reportRepository = reportRepository;
        this.dataCollector = dataCollector;
        this.summaryBuilder = summaryBuilder;
        this.promptContextBuilder = promptContextBuilder;
        this.aiAnalyzer = aiAnalyzer;
    }

    /**
     * 스케줄러 전용 생성: 당일 보고서가 이미 존재하면 건너뜀
     */
    public void generateIfAbsent(LocalDate targetDt) {
        MonitoringReport existing = reportRepository.findByReportDt(targetDt);
        if (existing != null) {
            log.info("Report for {} already exists (reportId={}). Skipping.", targetDt, existing.getReportId());
            return;
        }
        generate(targetDt);
    }

    /**
     * 수동 생성: 기존 보고서가 있으면 덮어씀
     */
    @Transactional
    public MonitoringReport generate(LocalDate targetDt) {
        LocalDateTime endDtm = LocalDateTime.now();
        LocalDateTime startDtm = endDtm.minusHours(24);

        MonitoringReport report = initReport(targetDt, startDtm, endDtm);

        ReportAnalysisContext context = new ReportAnalysisContext();
        context.setAnalysisStartDtm(startDtm);
        context.setAnalysisEndDtm(endDtm);

        try {
            dataCollector.collect(context);
            summaryBuilder.build(context);
            promptContextBuilder.build(context);
            aiAnalyzer.analyze(context);

            report.setTotalExecCnt(context.getTotalExecCnt());
            report.setSuccessCnt(context.getSuccessCnt());
            report.setFailureCnt(context.getFailureCnt());
            report.setErrorCnt(context.getErrorCnt());
            report.setExecSummary(context.getExecSummary());
            report.setAiAdvice(context.getAiAdvice());
            report.setReportStatus("SUCCESS");

        } catch (Exception e) {
            log.error("Report generation failed for {}: {}", targetDt, e.getMessage(), e);
            report.setReportStatus("ERROR");
            report.setErrorMsg(truncate(e.getMessage(), 2000));
        }

        report.setFnlUptDtm(LocalDateTime.now());
        reportRepository.update(report);
        return report;
    }

    private MonitoringReport initReport(LocalDate targetDt, LocalDateTime startDtm, LocalDateTime endDtm) {
        LocalDateTime now = LocalDateTime.now();

        MonitoringReport existing = reportRepository.findByReportDt(targetDt);
        if (existing != null) {
            existing.setAnalysisStartDtm(startDtm);
            existing.setAnalysisEndDtm(endDtm);
            existing.setReportStatus("PENDING");
            existing.setErrorMsg(null);
            existing.setFnlUptDtm(now);
            reportRepository.update(existing);
            return existing;
        }

        MonitoringReport report = new MonitoringReport();
        report.setReportId(reportRepository.nextReportId());
        report.setReportDt(targetDt);
        report.setAnalysisStartDtm(startDtm);
        report.setAnalysisEndDtm(endDtm);
        report.setReportStatus("PENDING");
        report.setFstRegDtm(now);
        report.setFnlUptDtm(now);
        reportRepository.insert(report);
        return report;
    }

    private String truncate(String value, int maxLength) {
        if (value == null) return null;
        return value.length() <= maxLength ? value : value.substring(0, maxLength);
    }
}
