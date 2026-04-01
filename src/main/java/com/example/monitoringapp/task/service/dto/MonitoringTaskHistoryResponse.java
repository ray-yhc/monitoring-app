package com.example.monitoringapp.task.service.dto;

import com.fasterxml.jackson.databind.JsonNode;

import java.time.LocalDateTime;

public class MonitoringTaskHistoryResponse {

    private Long taskHistId;
    private Long taskId;
    private LocalDateTime execDtm;
    private String execTrgTypeCd;
    private String execRslt;
    private String execRsltMsg;
    private JsonNode execRsltData;
    private String alertEventType;
    private Long execDurMs;

    public Long getTaskHistId() { return taskHistId; }
    public void setTaskHistId(Long taskHistId) { this.taskHistId = taskHistId; }
    public Long getTaskId() { return taskId; }
    public void setTaskId(Long taskId) { this.taskId = taskId; }
    public LocalDateTime getExecDtm() { return execDtm; }
    public void setExecDtm(LocalDateTime execDtm) { this.execDtm = execDtm; }
    public String getExecTrgTypeCd() { return execTrgTypeCd; }
    public void setExecTrgTypeCd(String execTrgTypeCd) { this.execTrgTypeCd = execTrgTypeCd; }
    public String getExecRslt() { return execRslt; }
    public void setExecRslt(String execRslt) { this.execRslt = execRslt; }
    public String getExecRsltMsg() { return execRsltMsg; }
    public void setExecRsltMsg(String execRsltMsg) { this.execRsltMsg = execRsltMsg; }
    public JsonNode getExecRsltData() { return execRsltData; }
    public void setExecRsltData(JsonNode execRsltData) { this.execRsltData = execRsltData; }
    public String getAlertEventType() { return alertEventType; }
    public void setAlertEventType(String alertEventType) { this.alertEventType = alertEventType; }
    public Long getExecDurMs() { return execDurMs; }
    public void setExecDurMs(Long execDurMs) { this.execDurMs = execDurMs; }
}