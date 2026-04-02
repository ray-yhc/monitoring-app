package com.example.monitoringapp.task.web;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.ArrayList;
import java.util.List;

public class MonitoringTaskForm {

    private Long taskId;
    @NotBlank
    private String taskNm;
    @NotBlank
    private String taskTypeCd;
    private String taskCntnt;
    @NotBlank
    private String execParam;
    @NotBlank
    private String successParam;
    @NotBlank
    private String scheduleVal;
    @NotNull
    @Min(0)
    @Max(9999)
    private Integer taskPrio = 100;
    @NotBlank
    private String activeYn = "Y";
    private List<Long> reportGroupIds = new ArrayList<>();

    public Long getTaskId() { return taskId; }
    public void setTaskId(Long taskId) { this.taskId = taskId; }
    public String getTaskNm() { return taskNm; }
    public void setTaskNm(String taskNm) { this.taskNm = taskNm; }
    public String getTaskTypeCd() { return taskTypeCd; }
    public void setTaskTypeCd(String taskTypeCd) { this.taskTypeCd = taskTypeCd; }
    public String getTaskCntnt() { return taskCntnt; }
    public void setTaskCntnt(String taskCntnt) { this.taskCntnt = taskCntnt; }
    public String getExecParam() { return execParam; }
    public void setExecParam(String execParam) { this.execParam = execParam; }
    public String getSuccessParam() { return successParam; }
    public void setSuccessParam(String successParam) { this.successParam = successParam; }
    public String getScheduleVal() { return scheduleVal; }
    public void setScheduleVal(String scheduleVal) { this.scheduleVal = scheduleVal; }
    public Integer getTaskPrio() { return taskPrio; }
    public void setTaskPrio(Integer taskPrio) { this.taskPrio = taskPrio; }
    public String getActiveYn() { return activeYn; }
    public void setActiveYn(String activeYn) { this.activeYn = activeYn; }
    public List<Long> getReportGroupIds() { return reportGroupIds; }
    public void setReportGroupIds(List<Long> reportGroupIds) { this.reportGroupIds = reportGroupIds; }
}