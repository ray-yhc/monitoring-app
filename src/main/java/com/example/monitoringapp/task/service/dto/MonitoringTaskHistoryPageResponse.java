package com.example.monitoringapp.task.service.dto;

import java.util.List;

public class MonitoringTaskHistoryPageResponse {

    private Long taskId;
    private int page;
    private int size;
    private long totalCount;
    private List<MonitoringTaskHistoryResponse> contents;

    public Long getTaskId() { return taskId; }
    public void setTaskId(Long taskId) { this.taskId = taskId; }
    public int getPage() { return page; }
    public void setPage(int page) { this.page = page; }
    public int getSize() { return size; }
    public void setSize(int size) { this.size = size; }
    public long getTotalCount() { return totalCount; }
    public void setTotalCount(long totalCount) { this.totalCount = totalCount; }
    public List<MonitoringTaskHistoryResponse> getContents() { return contents; }
    public void setContents(List<MonitoringTaskHistoryResponse> contents) { this.contents = contents; }
}