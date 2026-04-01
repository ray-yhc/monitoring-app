package com.example.monitoringapp.alert;

import com.example.monitoringapp.task.domain.AlertEventType;
import com.example.monitoringapp.task.domain.TaskResult;
import org.springframework.stereotype.Service;

@Service
public class AlertPolicyService {

    public AlertEventType determine(TaskResult previousResult, TaskResult currentResult) {
        if (currentResult == null) {
            return AlertEventType.NONE;
        }
        if (currentResult != TaskResult.SUCCESS) {
            return AlertEventType.ALERT_TRIGGERED;
        }
        if (previousResult != null && previousResult != TaskResult.SUCCESS) {
            return AlertEventType.ALERT_RESOLVED;
        }
        return AlertEventType.NONE;
    }
}