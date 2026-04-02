package com.example.monitoringapp.task.service.dto;

public class ReportGroupResponse {

    private Long reportGroupId;
    private String reportGroupName;
    private String description;
    private String chatRoomId;
    private String sendYn;

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
}