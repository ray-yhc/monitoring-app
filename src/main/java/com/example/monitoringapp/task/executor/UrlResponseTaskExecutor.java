package com.example.monitoringapp.task.executor;

import com.example.monitoringapp.common.json.JsonSupport;
import com.example.monitoringapp.config.MonitoringProperties;
import com.example.monitoringapp.task.domain.AlertEventType;
import com.example.monitoringapp.task.domain.TaskExecutionContext;
import com.example.monitoringapp.task.domain.TaskExecutionResult;
import com.example.monitoringapp.task.domain.TaskResult;
import com.example.monitoringapp.task.domain.TaskType;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class UrlResponseTaskExecutor extends AbstractHttpTaskExecutor {

    public UrlResponseTaskExecutor(
            WebClient.Builder webClientBuilder,
            JsonSupport jsonSupport,
            MonitoringProperties monitoringProperties
    ) {
        super(webClientBuilder, jsonSupport, monitoringProperties.getExecution().getDefaultTimeoutMs());
    }

    @Override
    public TaskType supports() {
        return TaskType.URL_RESPONSE_CHECK;
    }

    @Override
    public TaskExecutionResult execute(TaskExecutionContext context) {
        HttpCallResult httpCallResult = executeHttp(context.getExecParam());
        int expectedStatusCode = context.getSuccessParam().path("expectedHttpStatusCode").asInt();
        long maxResponseTimeMs = context.getSuccessParam().path("maxResponseTimeMs").asLong(Long.MAX_VALUE);
        String containsText = context.getSuccessParam().path("containsText").asText("");
        boolean bodyMatched = containsText.isBlank() || httpCallResult.body().contains(containsText);
        boolean success = httpCallResult.statusCode() == expectedStatusCode
                && httpCallResult.durationMs() <= maxResponseTimeMs
                && bodyMatched;

        ObjectNode resultData = jsonSupport.objectNode();
        resultData.put("httpStatusCode", httpCallResult.statusCode());
        resultData.put("durationMs", httpCallResult.durationMs());
        resultData.put("bodyPreview", abbreviate(httpCallResult.body()));

        return new TaskExecutionResult(
                success ? TaskResult.SUCCESS : TaskResult.FAILURE,
                "httpStatusCode=" + httpCallResult.statusCode(),
                resultData,
                httpCallResult.durationMs(),
                AlertEventType.NONE
        );
    }

    private String abbreviate(String body) {
        if (body == null) {
            return "";
        }
        return body.length() <= 200 ? body : body.substring(0, 200);
    }
}