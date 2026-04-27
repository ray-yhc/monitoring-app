package com.example.monitoringapp.report.domain;

import java.time.LocalDateTime;
import java.util.List;

public class ReportAnalysisContext {

    private LocalDateTime analysisStartDtm;
    private LocalDateTime analysisEndDtm;

    // Step 1: collectData 결과
    private List<TaskExecutionStats> taskStatsList;
    private int totalExecCnt;
    private int successCnt;
    private int failureCnt;
    private int errorCnt;

    // Step 2: buildSummary 결과
    private String execSummary;

    // Step 3: buildPromptContext 결과
    private String promptContext;

    // Step 4: callAi 결과
    private String aiAdvice;

    public LocalDateTime getAnalysisStartDtm() { return analysisStartDtm; }
    public void setAnalysisStartDtm(LocalDateTime analysisStartDtm) { this.analysisStartDtm = analysisStartDtm; }
    public LocalDateTime getAnalysisEndDtm() { return analysisEndDtm; }
    public void setAnalysisEndDtm(LocalDateTime analysisEndDtm) { this.analysisEndDtm = analysisEndDtm; }
    public List<TaskExecutionStats> getTaskStatsList() { return taskStatsList; }
    public void setTaskStatsList(List<TaskExecutionStats> taskStatsList) { this.taskStatsList = taskStatsList; }
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
    public String getPromptContext() { return promptContext; }
    public void setPromptContext(String promptContext) { this.promptContext = promptContext; }
    public String getAiAdvice() { return aiAdvice; }
    public void setAiAdvice(String aiAdvice) { this.aiAdvice = aiAdvice; }
}
