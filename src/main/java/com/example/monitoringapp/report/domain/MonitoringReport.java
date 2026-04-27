package com.example.monitoringapp.report.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class MonitoringReport {

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

    public Long getReportId() { return reportId; }
    public void setReportId(Long reportId) { this.reportId = reportId; }
    public LocalDate getReportDt() { return reportDt; }
    public void setReportDt(LocalDate reportDt) { this.reportDt = reportDt; }
    public LocalDateTime getAnalysisStartDtm() { return analysisStartDtm; }
    public void setAnalysisStartDtm(LocalDateTime analysisStartDtm) { this.analysisStartDtm = analysisStartDtm; }
    public LocalDateTime getAnalysisEndDtm() { return analysisEndDtm; }
    public void setAnalysisEndDtm(LocalDateTime analysisEndDtm) { this.analysisEndDtm = analysisEndDtm; }
    public int getTotalExecCnt() { return totalExecCnt; }
    public void setTotalExecCnt(int totalExecCnt) { this.totalExecCnt = totalExecCnt; }
    public int getSuccessCnt() { return successCnt; }
    public void setSuccessCnt(int successCnt) { this.successCnt = successCnt; }
    public int getFailureCnt() { return failureCnt; }
    public void setFailureCnt(int failureCnt) { this.failureCnt = failureCnt; }
    public int getErrorCnt() { return errorCnt; }
    public void setErrorCnt(int errorCnt) { this.errorCnt = errorCnt; }
    public String getExecSummary() { return execSummary; }
    public void setExecSummary(String execSummary) { this.execSummary = execSummary; }
    public String getAiAdvice() { return aiAdvice; }
    public void setAiAdvice(String aiAdvice) { this.aiAdvice = aiAdvice; }
    public String getReportStatus() { return reportStatus; }
    public void setReportStatus(String reportStatus) { this.reportStatus = reportStatus; }
    public String getErrorMsg() { return errorMsg; }
    public void setErrorMsg(String errorMsg) { this.errorMsg = errorMsg; }
    public LocalDateTime getFstRegDtm() { return fstRegDtm; }
    public void setFstRegDtm(LocalDateTime fstRegDtm) { this.fstRegDtm = fstRegDtm; }
    public LocalDateTime getFnlUptDtm() { return fnlUptDtm; }
    public void setFnlUptDtm(LocalDateTime fnlUptDtm) { this.fnlUptDtm = fnlUptDtm; }
}
