package com.example.monitoringapp.task.domain;

import java.time.LocalDateTime;

public class MonitoringTaskHistory {

    private Long taskHistId;
    private Long taskId;
    private LocalDateTime execDtm;
    private String execTrgTypeCd;
    private String execRslt;
    private String execRsltMsg;
    private String execRsltDataJson;
    private String alertEventType;
    private Long execDurMs;
    private LocalDateTime fstRegDtm;
    private LocalDateTime fnlUptDtm;

    public Long getTaskHistId() {
        return taskHistId;
    }

    public void setTaskHistId(Long taskHistId) {
        this.taskHistId = taskHistId;
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public LocalDateTime getExecDtm() {
        return execDtm;
    }

    public void setExecDtm(LocalDateTime execDtm) {
        this.execDtm = execDtm;
    }

    public String getExecTrgTypeCd() {
        return execTrgTypeCd;
    }

    public void setExecTrgTypeCd(String execTrgTypeCd) {
        this.execTrgTypeCd = execTrgTypeCd;
    }

    public String getExecRslt() {
        return execRslt;
    }

    public void setExecRslt(String execRslt) {
        this.execRslt = execRslt;
    }

    public String getExecRsltMsg() {
        return execRsltMsg;
    }

    public void setExecRsltMsg(String execRsltMsg) {
        this.execRsltMsg = execRsltMsg;
    }

    public String getExecRsltDataJson() {
        return execRsltDataJson;
    }

    public void setExecRsltDataJson(String execRsltDataJson) {
        this.execRsltDataJson = execRsltDataJson;
    }

    public String getAlertEventType() {
        return alertEventType;
    }

    public void setAlertEventType(String alertEventType) {
        this.alertEventType = alertEventType;
    }

    public Long getExecDurMs() {
        return execDurMs;
    }

    public void setExecDurMs(Long execDurMs) {
        this.execDurMs = execDurMs;
    }

    public LocalDateTime getFstRegDtm() {
        return fstRegDtm;
    }

    public void setFstRegDtm(LocalDateTime fstRegDtm) {
        this.fstRegDtm = fstRegDtm;
    }

    public LocalDateTime getFnlUptDtm() {
        return fnlUptDtm;
    }

    public void setFnlUptDtm(LocalDateTime fnlUptDtm) {
        this.fnlUptDtm = fnlUptDtm;
    }
}