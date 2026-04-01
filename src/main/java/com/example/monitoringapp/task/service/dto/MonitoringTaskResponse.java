package com.example.monitoringapp.task.service.dto;

import com.fasterxml.jackson.databind.JsonNode;

import java.time.LocalDateTime;

public class MonitoringTaskResponse {

    private Long taskId;
    private String taskNm;
    private String taskTypeCd;
    private String taskCntnt;
    private JsonNode execParam;
    private JsonNode successParam;
    private String scheduleVal;
    private Integer taskPrio;
    private String activeYn;
    private LocalDateTime lastExecDtm;
    private String lastExecRslt;
    private String lastExecRsltMsg;

    public Long getTaskId() { return taskId; }
    public void setTaskId(Long taskId) { this.taskId = taskId; }
    public String getTaskNm() { return taskNm; }
    public void setTaskNm(String taskNm) { this.taskNm = taskNm; }
    public String getTaskTypeCd() { return taskTypeCd; }
    public void setTaskTypeCd(String taskTypeCd) { this.taskTypeCd = taskTypeCd; }
    public String getTaskCntnt() { return taskCntnt; }
    public void setTaskCntnt(String taskCntnt) { this.taskCntnt = taskCntnt; }
    public JsonNode getExecParam() { return execParam; }
    public void setExecParam(JsonNode execParam) { this.execParam = execParam; }
    public JsonNode getSuccessParam() { return successParam; }
    public void setSuccessParam(JsonNode successParam) { this.successParam = successParam; }
    public String getScheduleVal() { return scheduleVal; }
    public void setScheduleVal(String scheduleVal) { this.scheduleVal = scheduleVal; }
    public Integer getTaskPrio() { return taskPrio; }
    public void setTaskPrio(Integer taskPrio) { this.taskPrio = taskPrio; }
    public String getActiveYn() { return activeYn; }
    public void setActiveYn(String activeYn) { this.activeYn = activeYn; }
    public LocalDateTime getLastExecDtm() { return lastExecDtm; }
    public void setLastExecDtm(LocalDateTime lastExecDtm) { this.lastExecDtm = lastExecDtm; }
    public String getLastExecRslt() { return lastExecRslt; }
    public void setLastExecRslt(String lastExecRslt) { this.lastExecRslt = lastExecRslt; }
    public String getLastExecRsltMsg() { return lastExecRsltMsg; }
    public void setLastExecRsltMsg(String lastExecRsltMsg) { this.lastExecRsltMsg = lastExecRsltMsg; }
}