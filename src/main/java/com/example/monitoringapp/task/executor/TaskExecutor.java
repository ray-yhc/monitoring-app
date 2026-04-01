package com.example.monitoringapp.task.executor;

import com.example.monitoringapp.task.domain.TaskExecutionContext;
import com.example.monitoringapp.task.domain.TaskExecutionResult;
import com.example.monitoringapp.task.domain.TaskType;

public interface TaskExecutor {

    TaskType supports();

    TaskExecutionResult execute(TaskExecutionContext context);
}