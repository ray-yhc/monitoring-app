package com.example.monitoringapp.task.service.dto;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class MonitoringTaskUpsertRequest {

    @NotBlank
    private String taskNm;
    @NotBlank
    private String taskTypeCd;
    private String taskCntnt;
    @NotNull
    private JsonNode execParam;
    @NotNull
    private JsonNode successParam;
    @NotBlank
    private String scheduleVal;
    @NotNull
    @Min(0)
    @Max(9999)
    private Integer taskPrio;
    @NotBlank
    private String activeYn;

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
}