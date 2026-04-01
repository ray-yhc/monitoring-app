package com.example.monitoringapp.task.domain;

import java.time.LocalDateTime;

public class MonitoringTask {

    private Long taskId;
    private String taskNm;
    private String taskTypeCd;
    private String taskCntnt;
    private String execParamJson;
    private String successParamJson;
    private String scheduleVal;
    private Integer taskPrio;
    private String activeYn;
    private LocalDateTime lastExecDtm;
    private String lastExecRslt;
    private String lastExecRsltMsg;
    private LocalDateTime fstRegDtm;
    private LocalDateTime fnlUptDtm;

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public String getTaskNm() {
        return taskNm;
    }

    public void setTaskNm(String taskNm) {
        this.taskNm = taskNm;
    }

    public String getTaskTypeCd() {
        return taskTypeCd;
    }

    public void setTaskTypeCd(String taskTypeCd) {
        this.taskTypeCd = taskTypeCd;
    }

    public String getTaskCntnt() {
        return taskCntnt;
    }

    public void setTaskCntnt(String taskCntnt) {
        this.taskCntnt = taskCntnt;
    }

    public String getExecParamJson() {
        return execParamJson;
    }

    public void setExecParamJson(String execParamJson) {
        this.execParamJson = execParamJson;
    }

    public String getSuccessParamJson() {
        return successParamJson;
    }

    public void setSuccessParamJson(String successParamJson) {
        this.successParamJson = successParamJson;
    }

    public String getScheduleVal() {
        return scheduleVal;
    }

    public void setScheduleVal(String scheduleVal) {
        this.scheduleVal = scheduleVal;
    }

    public Integer getTaskPrio() {
        return taskPrio;
    }

    public void setTaskPrio(Integer taskPrio) {
        this.taskPrio = taskPrio;
    }

    public String getActiveYn() {
        return activeYn;
    }

    public void setActiveYn(String activeYn) {
        this.activeYn = activeYn;
    }

    public LocalDateTime getLastExecDtm() {
        return lastExecDtm;
    }

    public void setLastExecDtm(LocalDateTime lastExecDtm) {
        this.lastExecDtm = lastExecDtm;
    }

    public String getLastExecRslt() {
        return lastExecRslt;
    }

    public void setLastExecRslt(String lastExecRslt) {
        this.lastExecRslt = lastExecRslt;
    }

    public String getLastExecRsltMsg() {
        return lastExecRsltMsg;
    }

    public void setLastExecRsltMsg(String lastExecRsltMsg) {
        this.lastExecRsltMsg = lastExecRsltMsg;
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