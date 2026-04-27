package com.example.monitoringapp.report.domain;

public class TaskExecutionStats {

    private Long taskId;
    private String taskNm;
    private String taskTypeCd;
    private int totalCnt;
    private int successCnt;
    private int failureCnt;
    private int errorCnt;

    public Long getTaskId() { return taskId; }
    public void setTaskId(Long taskId) { this.taskId = taskId; }
    public String getTaskNm() { return taskNm; }
    public void setTaskNm(String taskNm) { this.taskNm = taskNm; }
    public String getTaskTypeCd() { return taskTypeCd; }
    public void setTaskTypeCd(String taskTypeCd) { this.taskTypeCd = taskTypeCd; }
    public int getTotalCnt() { return totalCnt; }
    public void setTotalCnt(int totalCnt) { this.totalCnt = totalCnt; }
    public int getSuccessCnt() { return successCnt; }
    public void setSuccessCnt(int successCnt) { this.successCnt = successCnt; }
    public int getFailureCnt() { return failureCnt; }
    public void setFailureCnt(int failureCnt) { this.failureCnt = failureCnt; }
    public int getErrorCnt() { return errorCnt; }
    public void setErrorCnt(int errorCnt) { this.errorCnt = errorCnt; }

    public int getIssueCount() {
        return failureCnt + errorCnt;
    }
}
