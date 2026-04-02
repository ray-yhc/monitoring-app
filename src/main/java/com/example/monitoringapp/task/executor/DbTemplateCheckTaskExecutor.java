package com.example.monitoringapp.task.executor;

import com.example.monitoringapp.common.json.JsonSupport;
import com.example.monitoringapp.task.domain.AlertEventType;
import com.example.monitoringapp.task.domain.TaskExecutionContext;
import com.example.monitoringapp.task.domain.TaskExecutionResult;
import com.example.monitoringapp.task.domain.TaskResult;
import com.example.monitoringapp.task.domain.TaskType;
import com.example.monitoringapp.task.service.ExternalDatabaseQueryService;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;

@Component
public class DbTemplateCheckTaskExecutor implements TaskExecutor {

    private static final String STATUS_SUCCESS = "SUCCESS";
    private static final String STATUS_FAIL = "FAIL";

    private final ExternalDatabaseQueryService externalDatabaseQueryService;
    private final JsonSupport jsonSupport;

    public DbTemplateCheckTaskExecutor(
            ExternalDatabaseQueryService externalDatabaseQueryService,
            JsonSupport jsonSupport
    ) {
        this.externalDatabaseQueryService = externalDatabaseQueryService;
        this.jsonSupport = jsonSupport;
    }

    @Override
    public TaskType supports() {
        return TaskType.DB_TEMPLATE_CHECK;
    }

    @Override
    public TaskExecutionResult execute(TaskExecutionContext context) {
        Instant start = Instant.now();
        String datasourceKey = context.getExecParam().path("db").asText();
        String query = context.getExecParam().path("query").asText();

        try {
            Map<String, Object> row = externalDatabaseQueryService.queryFirstRow(datasourceKey, query);
            String status = findValueIgnoreCase(row, "STATUS");
            String message = findValueIgnoreCase(row, "MESSAGE");

            long durationMs = Duration.between(start, Instant.now()).toMillis();
            ObjectNode resultData = jsonSupport.objectNode();
            resultData.put("status", status == null ? "" : status);
            resultData.put("message", message == null ? "" : message);
            resultData.put("db", datasourceKey);
            resultData.put("query", query);

            if (status == null || status.isBlank()) {
                return new TaskExecutionResult(
                        TaskResult.ERROR,
                        "Invalid template response: STATUS column missing",
                        resultData,
                        durationMs,
                        AlertEventType.NONE
                );
            }

            if (message == null) {
                return new TaskExecutionResult(
                        TaskResult.ERROR,
                        "Invalid template response: MESSAGE column missing",
                        resultData,
                        durationMs,
                        AlertEventType.NONE
                );
            }

            if (STATUS_SUCCESS.equalsIgnoreCase(status)) {
                return new TaskExecutionResult(
                        TaskResult.SUCCESS,
                        message,
                        resultData,
                        durationMs,
                        AlertEventType.NONE
                );
            }

            if (STATUS_FAIL.equalsIgnoreCase(status)) {
                return new TaskExecutionResult(
                        TaskResult.FAILURE,
                        message,
                        resultData,
                        durationMs,
                        AlertEventType.NONE
                );
            }

            return new TaskExecutionResult(
                    TaskResult.ERROR,
                    "Invalid template response: STATUS must be SUCCESS or FAIL",
                    resultData,
                    durationMs,
                    AlertEventType.NONE
            );
        } catch (Exception e) {
            long durationMs = Duration.between(start, Instant.now()).toMillis();
            ObjectNode resultData = jsonSupport.objectNode();
            resultData.put("db", datasourceKey);
            resultData.put("query", query);
            resultData.put("errorType", e.getClass().getSimpleName());

            return new TaskExecutionResult(
                    TaskResult.ERROR,
                    "Query execution error: " + safeMessage(e),
                    resultData,
                    durationMs,
                    AlertEventType.NONE
            );
        }
    }

    private String findValueIgnoreCase(Map<String, Object> row, String targetKey) {
        for (Map.Entry<String, Object> entry : row.entrySet()) {
            if (entry.getKey() != null && targetKey.equalsIgnoreCase(entry.getKey())) {
                Object value = entry.getValue();
                return value == null ? null : value.toString();
            }
        }
        return null;
    }

    private String safeMessage(Exception e) {
        return e.getMessage() == null || e.getMessage().isBlank() ? e.getClass().getSimpleName() : e.getMessage();
    }
}