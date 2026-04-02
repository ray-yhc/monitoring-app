package com.example.monitoringapp.task.domain;

public enum TaskType {
    URL_HEALTH_CHECK,
    URL_RESPONSE_CHECK,
    DB_QUERY_CHECK,
    DB_TEMPLATE_CHECK,
    ES_LOG_CHECK,
    PROM_QL_CHECK
}