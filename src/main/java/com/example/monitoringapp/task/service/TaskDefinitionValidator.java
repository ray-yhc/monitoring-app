package com.example.monitoringapp.task.service;

import com.example.monitoringapp.task.domain.TaskType;
import com.example.monitoringapp.task.service.dto.MonitoringTaskUpsertRequest;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.scheduling.support.CronExpression;
import org.springframework.stereotype.Component;

@Component
public class TaskDefinitionValidator {

    public void validate(MonitoringTaskUpsertRequest request) {
        TaskType taskType = TaskType.valueOf(request.getTaskTypeCd());
        CronExpression.parse(request.getScheduleVal());

        if (!"Y".equals(request.getActiveYn()) && !"N".equals(request.getActiveYn())) {
            throw new IllegalArgumentException("activeYn must be Y or N");
        }

        if (request.getReportGroupIds() != null) {
            for (Long reportGroupId : request.getReportGroupIds()) {
                if (reportGroupId == null || reportGroupId <= 0L) {
                    throw new IllegalArgumentException("reportGroupIds must contain positive numbers only");
                }
            }
        }

        switch (taskType) {
            case URL_HEALTH_CHECK -> {
                requireText(request.getExecParam(), "url");
                requireText(request.getExecParam(), "method");
                requireNode(request.getSuccessParam(), "expectedHttpStatusCode");
                requireText(request.getSuccessParam(), "expectedStatus");
            }
            case URL_RESPONSE_CHECK -> {
                requireText(request.getExecParam(), "url");
                requireText(request.getExecParam(), "method");
                requireNode(request.getSuccessParam(), "expectedHttpStatusCode");
            }
            case DB_QUERY_CHECK -> {
                requireText(request.getExecParam(), "db");
                requireText(request.getExecParam(), "query");
                requireText(request.getSuccessParam(), "comparison");
                requireNode(request.getSuccessParam(), "expectedValue");
            }
            case DB_TEMPLATE_CHECK -> {
                requireText(request.getExecParam(), "db");
                requireText(request.getExecParam(), "query");
            }
            case ES_LOG_CHECK -> {
                requireText(request.getExecParam(), "url");
                requireText(request.getExecParam(), "method");
                requireNode(request.getExecParam(), "body");
                requireText(request.getSuccessParam(), "comparison");
                requireNode(request.getSuccessParam(), "expectedValue");
                requireText(request.getSuccessParam(), "valuePath");
            }
            case PROM_QL_CHECK -> {
                requireText(request.getExecParam(), "url");
                JsonNode queryParams = requireNode(request.getExecParam(), "queryParams");
                requireText(queryParams, "query");
                requireText(request.getSuccessParam(), "comparison");
                requireNode(request.getSuccessParam(), "expectedValue");
                requireText(request.getSuccessParam(), "valuePath");
            }
        }
    }

    private JsonNode requireNode(JsonNode jsonNode, String fieldName) {
        if (jsonNode == null || jsonNode.get(fieldName) == null || jsonNode.get(fieldName).isNull()) {
            throw new IllegalArgumentException(fieldName + " is required");
        }
        return jsonNode.get(fieldName);
    }

    private void requireText(JsonNode jsonNode, String fieldName) {
        JsonNode node = requireNode(jsonNode, fieldName);
        if (!node.isTextual() || node.textValue().isBlank()) {
            throw new IllegalArgumentException(fieldName + " must not be blank");
        }
    }
}