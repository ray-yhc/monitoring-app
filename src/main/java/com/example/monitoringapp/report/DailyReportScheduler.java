package com.example.monitoringapp.report;

import com.example.monitoringapp.config.MonitoringProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class DailyReportScheduler {

    private static final Logger log = LoggerFactory.getLogger(DailyReportScheduler.class);

    private final MonitoringProperties monitoringProperties;
    private final ReportGenerationService reportGenerationService;

    public DailyReportScheduler(
            MonitoringProperties monitoringProperties,
            ReportGenerationService reportGenerationService
    ) {
        this.monitoringProperties = monitoringProperties;
        this.reportGenerationService = reportGenerationService;
    }

    @Scheduled(cron = "${monitoring.report.schedule:0 0 9 * * *}")
    public void generateDailyReport() {
        if (!monitoringProperties.getReport().isEnabled()) {
            return;
        }
        log.info("Daily report generation started.");
        try {
            reportGenerationService.generateIfAbsent(LocalDate.now());
            log.info("Daily report generation completed.");
        } catch (Exception e) {
            log.error("Daily report generation failed unexpectedly: {}", e.getMessage(), e);
        }
    }
}
