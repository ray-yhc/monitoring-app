package com.example.monitoringapp.task.rest;

import com.example.monitoringapp.task.service.ReportGroupQueryService;
import com.example.monitoringapp.task.service.dto.ReportGroupResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/monitoring-app/report-groups")
public class ReportGroupRestController {

    private final ReportGroupQueryService reportGroupQueryService;

    public ReportGroupRestController(ReportGroupQueryService reportGroupQueryService) {
        this.reportGroupQueryService = reportGroupQueryService;
    }

    @GetMapping
    public List<ReportGroupResponse> getReportGroups() {
        return reportGroupQueryService.getReportGroups();
    }

    @GetMapping("/{reportGroupId}")
    public ReportGroupResponse getReportGroup(@PathVariable Long reportGroupId) {
        return reportGroupQueryService.getReportGroup(reportGroupId);
    }
}