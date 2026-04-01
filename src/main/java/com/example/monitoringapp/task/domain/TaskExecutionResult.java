package com.example.monitoringapp.task.domain;

import com.fasterxml.jackson.databind.node.ObjectNode;

public class TaskExecutionResult {

    private final TaskResult result;
    private final String message;
    private final ObjectNode resultData;
    private final long durationMs;
    private final AlertEventType alertEventType;

    public TaskExecutionResult(
            TaskResult result,
            String message,
            ObjectNode resultData,
            long durationMs,
            AlertEventType alertEventType
    ) {
        this.result = result;
        this.message = message;
        this.resultData = resultData;
        this.durationMs = durationMs;
        this.alertEventType = alertEventType;
    }

    public TaskResult getResult() {
        return result;
    }

    public String getMessage() {
        return message;
    }

    public ObjectNode getResultData() {
        return resultData;
    }

    public long getDurationMs() {
        return durationMs;
    }

    public AlertEventType getAlertEventType() {
        return alertEventType;
    }

    public TaskExecutionResult withAlertEventType(AlertEventType nextAlertEventType) {
        return new TaskExecutionResult(result, message, resultData, durationMs, nextAlertEventType);
    }
}