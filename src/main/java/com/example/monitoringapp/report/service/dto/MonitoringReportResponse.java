package com.example.monitoringapp.report.service.dto;

import com.example.monitoringapp.report.domain.MonitoringReport;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class MonitoringReportResponse {

    private Long reportId;
    private LocalDate reportDt;
    private LocalDateTime analysisStartDtm;
    private LocalDateTime analysisEndDtm;
    private int totalExecCnt;
    private int successCnt;
    private int failureCnt;
    private int errorCnt;
    private String execSummary;
    private String aiAdvice;
    private String reportStatus;
    private String errorMsg;
    private LocalDateTime fstRegDtm;
    private LocalDateTime fnlUptDtm;

    public static MonitoringReportResponse from(MonitoringReport report) {
        MonitoringReportResponse r = new MonitoringReportResponse();
        r.reportId         = report.getReportId();
        r.reportDt         = report.getReportDt();
        r.analysisStartDtm = report.getAnalysisStartDtm();
        r.analysisEndDtm   = report.getAnalysisEndDtm();
        r.totalExecCnt     = report.getTotalExecCnt();
        r.successCnt       = report.getSuccessCnt();
        r.failureCnt       = report.getFailureCnt();
        r.errorCnt         = report.getErrorCnt();
        r.execSummary      = report.getExecSummary();
        r.aiAdvice         = report.getAiAdvice();
        r.reportStatus     = report.getReportStatus();
        r.errorMsg         = report.getErrorMsg();
        r.fstRegDtm        = report.getFstRegDtm();
        r.fnlUptDtm        = report.getFnlUptDtm();
        return r;
    }

    public Long getReportId() { return reportId; }
    public LocalDate getReportDt() { return reportDt; }
    public LocalDateTime getAnalysisStartDtm() { return analysisStartDtm; }
    public LocalDateTime getAnalysisEndDtm() { return analysisEndDtm; }
    public int getTotalExecCnt() { return totalExecCnt; }
    public int getSuccessCnt() { return successCnt; }
    public int getFailureCnt() { return failureCnt; }
    public int getErrorCnt() { return errorCnt; }
    public String getExecSummary() { return execSummary; }
    public String getAiAdvice() { return aiAdvice; }
    public String getReportStatus() { return reportStatus; }
    public String getErrorMsg() { return errorMsg; }
    public LocalDateTime getFstRegDtm() { return fstRegDtm; }
    public LocalDateTime getFnlUptDtm() { return fnlUptDtm; }
}
