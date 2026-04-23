package com.example.monitoringapp.task.executor;

import com.example.monitoringapp.common.json.JsonSupport;
import com.example.monitoringapp.common.json.JsonValueExtractor;
import com.example.monitoringapp.config.MonitoringProperties;
import com.example.monitoringapp.task.domain.AlertEventType;
import com.example.monitoringapp.task.domain.ComparisonType;
import com.example.monitoringapp.task.domain.TaskExecutionContext;
import com.example.monitoringapp.task.domain.TaskExecutionResult;
import com.example.monitoringapp.task.domain.TaskResult;
import com.example.monitoringapp.task.domain.TaskType;
import com.example.monitoringapp.task.service.ComparisonEvaluator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class PromQlCheckTaskExecutor extends AbstractHttpTaskExecutor {

    private final MonitoringProperties monitoringProperties;
    private final ComparisonEvaluator comparisonEvaluator;
    private final JsonValueExtractor jsonValueExtractor;

    public PromQlCheckTaskExecutor(
            WebClient.Builder webClientBuilder,
            JsonSupport jsonSupport,
            MonitoringProperties monitoringProperties,
            ComparisonEvaluator comparisonEvaluator,
            JsonValueExtractor jsonValueExtractor
    ) {
        super(webClientBuilder, jsonSupport, monitoringProperties.getExecution().getDefaultTimeoutMs());
        this.monitoringProperties = monitoringProperties;
        this.comparisonEvaluator = comparisonEvaluator;
        this.jsonValueExtractor = jsonValueExtractor;
    }

    @Override
    public TaskType supports() {
        return TaskType.PROM_QL_CHECK;
    }

    @Override
    public TaskExecutionResult execute(TaskExecutionContext context) {
        String baseUrl = monitoringProperties.getPrometheus().getBaseUrl();
        HttpCallResult httpCallResult = executeHttp(baseUrl, context.getExecParam());
        JsonNode bodyJson = readBodyAsJson(httpCallResult.body());
        String valuePath = context.getSuccessParam().path("valuePath").asText();
        JsonNode actualNode = jsonValueExtractor.extract(bodyJson, valuePath);
        ComparisonType comparisonType = ComparisonType.valueOf(context.getSuccessParam().path("comparison").asText());
        boolean success = comparisonEvaluator.evaluate(actualNode, comparisonType, context.getSuccessParam().get("expectedValue"));

        ObjectNode resultData = jsonSupport.objectNode();
        resultData.put("httpStatusCode", httpCallResult.statusCode());
        resultData.put("valuePath", valuePath);
        resultData.put("actualValue", actualNode == null ? "null" : actualNode.asText(actualNode.toString()));

        return new TaskExecutionResult(
                success ? TaskResult.SUCCESS : TaskResult.FAILURE,
                "actualValue=" + resultData.get("actualValue").asText(),
                resultData,
                httpCallResult.durationMs(),
                AlertEventType.NONE
        );
    }
}