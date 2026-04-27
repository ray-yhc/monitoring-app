package com.example.monitoringapp.report.service.dto;

import java.util.List;

public class MonitoringReportPageResponse {

    private int page;
    private int size;
    private long totalCount;
    private List<MonitoringReportResponse> contents;

    public int getPage() { return page; }
    public void setPage(int page) { this.page = page; }
    public int getSize() { return size; }
    public void setSize(int size) { this.size = size; }
    public long getTotalCount() { return totalCount; }
    public void setTotalCount(long totalCount) { this.totalCount = totalCount; }
    public List<MonitoringReportResponse> getContents() { return contents; }
    public void setContents(List<MonitoringReportResponse> contents) { this.contents = contents; }
}
