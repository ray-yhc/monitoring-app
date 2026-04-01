package com.example.monitoringapp.task.rest;

import com.example.monitoringapp.task.domain.TaskExecutionResult;
import com.example.monitoringapp.task.service.MonitoringTaskCommandService;
import com.example.monitoringapp.task.service.MonitoringTaskExecutionService;
import com.example.monitoringapp.task.service.MonitoringTaskQueryService;
import com.example.monitoringapp.task.service.dto.MonitoringTaskResponse;
import com.example.monitoringapp.task.service.dto.MonitoringTaskUpsertRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/monitoring-app/tasks")
public class MonitoringTaskRestController {

    private final MonitoringTaskQueryService monitoringTaskQueryService;
    private final MonitoringTaskCommandService monitoringTaskCommandService;
    private final MonitoringTaskExecutionService monitoringTaskExecutionService;

    public MonitoringTaskRestController(
            MonitoringTaskQueryService monitoringTaskQueryService,
            MonitoringTaskCommandService monitoringTaskCommandService,
            MonitoringTaskExecutionService monitoringTaskExecutionService
    ) {
        this.monitoringTaskQueryService = monitoringTaskQueryService;
        this.monitoringTaskCommandService = monitoringTaskCommandService;
        this.monitoringTaskExecutionService = monitoringTaskExecutionService;
    }

    @GetMapping
    public List<MonitoringTaskResponse> getTasks() {
        return monitoringTaskQueryService.getTasks();
    }

    @GetMapping("/{taskId}")
    public MonitoringTaskResponse getTask(@PathVariable Long taskId) {
        return monitoringTaskQueryService.getTask(taskId);
    }

    @PostMapping
    public MonitoringTaskResponse createTask(@Valid @RequestBody MonitoringTaskUpsertRequest request) {
        return monitoringTaskCommandService.create(request);
    }

    @PutMapping("/{taskId}")
    public MonitoringTaskResponse updateTask(
            @PathVariable Long taskId,
            @Valid @RequestBody MonitoringTaskUpsertRequest request
    ) {
        return monitoringTaskCommandService.update(taskId, request);
    }

    @DeleteMapping("/{taskId}")
    public void deleteTask(@PathVariable Long taskId) {
        monitoringTaskCommandService.deactivate(taskId);
    }

    @PostMapping("/{taskId}/activate")
    public void activateTask(@PathVariable Long taskId) {
        monitoringTaskCommandService.activate(taskId);
    }

    @PostMapping("/{taskId}/restart")
    public TaskExecutionResult restartTask(@PathVariable Long taskId) {
        return monitoringTaskExecutionService.executeNow(taskId);
    }
}