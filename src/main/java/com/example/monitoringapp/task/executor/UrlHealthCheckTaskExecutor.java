package com.example.monitoringapp.task.executor;

import com.example.monitoringapp.common.json.JsonSupport;
import com.example.monitoringapp.config.MonitoringProperties;
import com.example.monitoringapp.task.domain.AlertEventType;
import com.example.monitoringapp.task.domain.TaskExecutionContext;
import com.example.monitoringapp.task.domain.TaskExecutionResult;
import com.example.monitoringapp.task.domain.TaskResult;
import com.example.monitoringapp.task.domain.TaskType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class UrlHealthCheckTaskExecutor extends AbstractHttpTaskExecutor {

    public UrlHealthCheckTaskExecutor(
            WebClient.Builder webClientBuilder,
            JsonSupport jsonSupport,
            MonitoringProperties monitoringProperties
    ) {
        super(webClientBuilder, jsonSupport, monitoringProperties.getExecution().getDefaultTimeoutMs());
    }

    @Override
    public TaskType supports() {
        return TaskType.URL_HEALTH_CHECK;
    }

    @Override
    public TaskExecutionResult execute(TaskExecutionContext context) {
        HttpCallResult httpCallResult = executeHttp(context.getExecParam());
        JsonNode bodyJson = readBodyAsJson(httpCallResult.body());
        int expectedStatusCode = context.getSuccessParam().path("expectedHttpStatusCode").asInt();
        String expectedStatus = context.getSuccessParam().path("expectedStatus").asText();
        String actualStatus = bodyJson.path("status").asText();
        boolean success = httpCallResult.statusCode() == expectedStatusCode && expectedStatus.equals(actualStatus);

        ObjectNode resultData = jsonSupport.objectNode();
        resultData.put("httpStatusCode", httpCallResult.statusCode());
        resultData.put("status", actualStatus);

        return new TaskExecutionResult(
                success ? TaskResult.SUCCESS : TaskResult.FAILURE,
                "status=" + actualStatus,
                resultData,
                httpCallResult.durationMs(),
                AlertEventType.NONE
        );
    }
}