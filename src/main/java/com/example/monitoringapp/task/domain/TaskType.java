package com.example.monitoringapp.task.domain;

public enum TaskType {

    URL_HEALTH_CHECK(
            """
            {
              "url": "http://localhost:8080/actuator/health",
              "method": "GET",
              "timeoutMs": 3000,
              "headers": {
                "Accept": "application/json"
              }
            }""",
            """
            {
              "expectedHttpStatusCode": 200,
              "expectedStatus": "UP"
            }"""
    ),

    URL_RESPONSE_CHECK(
            """
            {
              "url": "https://example.com/api/status",
              "method": "GET",
              "timeoutMs": 5000,
              "headers": {
                "Authorization": "Bearer token"
              }
            }""",
            """
            {
              "expectedHttpStatusCode": 200,
              "maxResponseTimeMs": 3000,
              "containsText": "OK"
            }"""
    ),

    DB_QUERY_CHECK(
            """
            {
              "db": "default",
              "query": "SELECT COUNT(*) FROM your_table WHERE status = 'INVALID'"
            }""",
            """
            {
              "comparison": "LESS_THAN",
              "expectedValue": 1
            }"""
    ),

    DB_TEMPLATE_CHECK(
            """
            {
              "db": "default",
              "query": "SELECT CASE WHEN COUNT(*) = 0 THEN 'SUCCESS' ELSE 'FAIL' END AS STATUS, CONCAT(COUNT(*), ' invalid rows') AS MESSAGE FROM orders WHERE status = 'INVALID'"
            }""",
            "{}"
    ),

    ES_LOG_CHECK(
            """
            {
              "uri": "/logs-*/_search",
              "method": "POST",
              "timeoutMs": 5000,
              "headers": {
                "Content-Type": "application/json"
              },
              "body": {
                "size": 0,
                "query": {
                  "bool": {
                    "filter": [
                      { "match": { "level": "ERROR" } },
                      { "range": { "@timestamp": { "gte": "now-5m" } } }
                    ]
                  }
                }
              }
            }""",
            """
            {
              "comparison": "LESS_THAN",
              "expectedValue": 1,
              "valuePath": "hits.total.value"
            }"""
    ),

    PROM_QL_CHECK(
            """
            {
              "uri": "/api/v1/query",
              "method": "GET",
              "timeoutMs": 5000,
              "queryParams": {
                "query": "up{job='monitoring-app'}"
              }
            }""",
            """
            {
              "comparison": "GREATER_THAN",
              "expectedValue": 0,
              "valuePath": "data.result[0].value[1]"
            }"""
    );

    private final String execParamExample;
    private final String successParamExample;

    TaskType(String execParamExample, String successParamExample) {
        this.execParamExample = execParamExample;
        this.successParamExample = successParamExample;
    }

    public String getExecParamExample() {
        return execParamExample;
    }

    public String getSuccessParamExample() {
        return successParamExample;
    }
}
