package com.example.monitoringapp.task.domain;

import java.time.LocalDateTime;

public class ReportGroup {

    private Long reportGroupId;
    private String reportGroupName;
    private String description;
    private String chatRoomId;
    private String sendYn;
    private LocalDateTime fstRegDtm;
    private LocalDateTime fnlUptDtm;

    public Long getReportGroupId() { return reportGroupId; }
    public void setReportGroupId(Long reportGroupId) { this.reportGroupId = reportGroupId; }
    public String getReportGroupName() { return reportGroupName; }
    public void setReportGroupName(String reportGroupName) { this.reportGroupName = reportGroupName; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getChatRoomId() { return chatRoomId; }
    public void setChatRoomId(String chatRoomId) { this.chatRoomId = chatRoomId; }
    public String getSendYn() { return sendYn; }
    public void setSendYn(String sendYn) { this.sendYn = sendYn; }
    public LocalDateTime getFstRegDtm() { return fstRegDtm; }
    public void setFstRegDtm(LocalDateTime fstRegDtm) { this.fstRegDtm = fstRegDtm; }
    public LocalDateTime getFnlUptDtm() { return fnlUptDtm; }
    public void setFnlUptDtm(LocalDateTime fnlUptDtm) { this.fnlUptDtm = fnlUptDtm; }
}