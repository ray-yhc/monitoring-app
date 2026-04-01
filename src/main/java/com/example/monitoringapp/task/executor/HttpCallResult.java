package com.example.monitoringapp.task.executor;

public record HttpCallResult(int statusCode, String body, long durationMs) {
}