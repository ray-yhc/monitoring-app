package com.example.monitoringapp.task.rest;

import com.example.monitoringapp.task.service.MonitoringTaskQueryService;
import com.example.monitoringapp.task.service.dto.MonitoringTaskHistoryPageResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/monitoring-app/tasks/{taskId}/histories")
public class MonitoringTaskHistoryRestController {

    private final MonitoringTaskQueryService monitoringTaskQueryService;

    public MonitoringTaskHistoryRestController(MonitoringTaskQueryService monitoringTaskQueryService) {
        this.monitoringTaskQueryService = monitoringTaskQueryService;
    }

    @GetMapping
    public MonitoringTaskHistoryPageResponse getTaskHistories(
            @PathVariable Long taskId,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size
    ) {
        return monitoringTaskQueryService.getTaskHistories(taskId, page, size);
    }
}