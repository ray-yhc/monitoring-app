package com.example.monitoringapp.task.executor;

import com.example.monitoringapp.common.json.JsonSupport;
import com.example.monitoringapp.task.domain.AlertEventType;
import com.example.monitoringapp.task.domain.ComparisonType;
import com.example.monitoringapp.task.domain.TaskExecutionContext;
import com.example.monitoringapp.task.domain.TaskExecutionResult;
import com.example.monitoringapp.task.domain.TaskResult;
import com.example.monitoringapp.task.domain.TaskType;
import com.example.monitoringapp.task.service.ComparisonEvaluator;
import com.example.monitoringapp.task.service.ExternalDatabaseQueryService;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;

@Component
public class DbQueryCheckTaskExecutor implements TaskExecutor {

    private final ExternalDatabaseQueryService externalDatabaseQueryService;
    private final ComparisonEvaluator comparisonEvaluator;
    private final JsonSupport jsonSupport;

    public DbQueryCheckTaskExecutor(
            ExternalDatabaseQueryService externalDatabaseQueryService,
            ComparisonEvaluator comparisonEvaluator,
            JsonSupport jsonSupport
    ) {
        this.externalDatabaseQueryService = externalDatabaseQueryService;
        this.comparisonEvaluator = comparisonEvaluator;
        this.jsonSupport = jsonSupport;
    }

    @Override
    public TaskType supports() {
        return TaskType.DB_QUERY_CHECK;
    }

    @Override
    public TaskExecutionResult execute(TaskExecutionContext context) {
        Instant start = Instant.now();
        String datasourceKey = context.getExecParam().path("db").asText();
        String query = context.getExecParam().path("query").asText();
        Object actualValue = externalDatabaseQueryService.queryScalar(datasourceKey, query);
        ComparisonType comparisonType = ComparisonType.valueOf(context.getSuccessParam().path("comparison").asText());
        boolean success = comparisonEvaluator.evaluate(actualValue, comparisonType, context.getSuccessParam().get("expectedValue"));
        long durationMs = Duration.between(start, Instant.now()).toMillis();

        ObjectNode resultData = jsonSupport.objectNode();
        resultData.put("db", datasourceKey);
        resultData.put("actualValue", actualValue == null ? "null" : actualValue.toString());
        resultData.put("query", query);

        return new TaskExecutionResult(
                success ? TaskResult.SUCCESS : TaskResult.FAILURE,
                "actualValue=" + actualValue,
                resultData,
                durationMs,
                AlertEventType.NONE
        );
    }
}