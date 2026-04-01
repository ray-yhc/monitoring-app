package com.example.monitoringapp.task.domain;

import com.fasterxml.jackson.databind.JsonNode;

import java.time.LocalDateTime;

public class TaskExecutionContext {

    private final MonitoringTask task;
    private final JsonNode execParam;
    private final JsonNode successParam;
    private final ExecutionTriggerType triggerType;
    private final LocalDateTime requestedAt;
    private final TaskResult previousResult;

    public TaskExecutionContext(
            MonitoringTask task,
            JsonNode execParam,
            JsonNode successParam,
            ExecutionTriggerType triggerType,
            LocalDateTime requestedAt,
            TaskResult previousResult
    ) {
        this.task = task;
        this.execParam = execParam;
        this.successParam = successParam;
        this.triggerType = triggerType;
        this.requestedAt = requestedAt;
        this.previousResult = previousResult;
    }

    public MonitoringTask getTask() {
        return task;
    }

    public JsonNode getExecParam() {
        return execParam;
    }

    public JsonNode getSuccessParam() {
        return successParam;
    }

    public ExecutionTriggerType getTriggerType() {
        return triggerType;
    }

    public LocalDateTime getRequestedAt() {
        return requestedAt;
    }

    public TaskResult getPreviousResult() {
        return previousResult;
    }
}