package com.example.monitoringapp.task.domain;

import java.time.LocalDateTime;

public class AlertEvent {

    private final Long taskId;
    private final String taskName;
    private final TaskResult previousResult;
    private final TaskResult currentResult;
    private final AlertEventType eventType;
    private final String message;
    private final LocalDateTime occurredAt;

    public AlertEvent(
            Long taskId,
            String taskName,
            TaskResult previousResult,
            TaskResult currentResult,
            AlertEventType eventType,
            String message,
            LocalDateTime occurredAt
    ) {
        this.taskId = taskId;
        this.taskName = taskName;
        this.previousResult = previousResult;
        this.currentResult = currentResult;
        this.eventType = eventType;
        this.message = message;
        this.occurredAt = occurredAt;
    }

    public Long getTaskId() {
        return taskId;
    }

    public String getTaskName() {
        return taskName;
    }

    public TaskResult getPreviousResult() {
        return previousResult;
    }

    public TaskResult getCurrentResult() {
        return currentResult;
    }

    public AlertEventType getEventType() {
        return eventType;
    }

    public String getMessage() {
        return message;
    }

    public LocalDateTime getOccurredAt() {
        return occurredAt;
    }
}