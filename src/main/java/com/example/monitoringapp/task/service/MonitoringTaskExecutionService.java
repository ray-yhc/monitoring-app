package com.example.monitoringapp.task.service;

import com.example.monitoringapp.alert.AlertNotifier;
import com.example.monitoringapp.alert.AlertPolicyService;
import com.example.monitoringapp.common.json.JsonSupport;
import com.example.monitoringapp.config.MonitoringProperties;
import com.example.monitoringapp.task.domain.AlertEvent;
import com.example.monitoringapp.task.domain.AlertEventType;
import com.example.monitoringapp.task.domain.ExecutionTriggerType;
import com.example.monitoringapp.task.domain.MonitoringTask;
import com.example.monitoringapp.task.domain.MonitoringTaskHistory;
import com.example.monitoringapp.task.domain.TaskExecutionContext;
import com.example.monitoringapp.task.domain.TaskExecutionResult;
import com.example.monitoringapp.task.domain.TaskResult;
import com.example.monitoringapp.task.domain.TaskType;
import com.example.monitoringapp.task.executor.TaskExecutor;
import com.example.monitoringapp.task.repository.defaultdb.MonitoringTaskHistoryRepository;
import com.example.monitoringapp.task.repository.defaultdb.MonitoringTaskRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Service
public class MonitoringTaskExecutionService {

    private static final Logger log = LoggerFactory.getLogger(MonitoringTaskExecutionService.class);

    private final MonitoringTaskRepository monitoringTaskRepository;
    private final MonitoringTaskHistoryRepository monitoringTaskHistoryRepository;
    private final MonitoringTaskQueryService monitoringTaskQueryService;
    private final TaskDueEvaluator taskDueEvaluator;
    private final AlertPolicyService alertPolicyService;
    private final AlertNotifier alertNotifier;
    private final JsonSupport jsonSupport;
    private final MonitoringProperties monitoringProperties;
    private final Map<TaskType, TaskExecutor> taskExecutors;

    public MonitoringTaskExecutionService(
            MonitoringTaskRepository monitoringTaskRepository,
            MonitoringTaskHistoryRepository monitoringTaskHistoryRepository,
            MonitoringTaskQueryService monitoringTaskQueryService,
            TaskDueEvaluator taskDueEvaluator,
            AlertPolicyService alertPolicyService,
            AlertNotifier alertNotifier,
            JsonSupport jsonSupport,
            MonitoringProperties monitoringProperties,
            List<TaskExecutor> taskExecutors
    ) {
        this.monitoringTaskRepository = monitoringTaskRepository;
        this.monitoringTaskHistoryRepository = monitoringTaskHistoryRepository;
        this.monitoringTaskQueryService = monitoringTaskQueryService;
        this.taskDueEvaluator = taskDueEvaluator;
        this.alertPolicyService = alertPolicyService;
        this.alertNotifier = alertNotifier;
        this.jsonSupport = jsonSupport;
        this.monitoringProperties = monitoringProperties;
        this.taskExecutors = new EnumMap<>(TaskType.class);
        for (TaskExecutor taskExecutor : taskExecutors) {
            this.taskExecutors.put(taskExecutor.supports(), taskExecutor);
        }
    }

    @Transactional
    public void executeDueTasks() {
        LocalDateTime now = LocalDateTime.now();
        List<MonitoringTask> dueTasks = monitoringTaskRepository.findAllActive().stream()
                .filter(task -> taskDueEvaluator.isDue(task, now))
                .toList();

        for (MonitoringTask task : dueTasks) {
            try {
                execute(task, ExecutionTriggerType.SCHEDULER, now);
            } catch (Exception e) {
                log.error("Failed to execute taskId={}", task.getTaskId(), e);
            }
        }
    }

    @Transactional
    public TaskExecutionResult executeNow(Long taskId) {
        MonitoringTask task = monitoringTaskQueryService.getTaskDomain(taskId);
        return execute(task, ExecutionTriggerType.MANUAL, LocalDateTime.now());
    }

    private TaskExecutionResult execute(MonitoringTask task, ExecutionTriggerType triggerType, LocalDateTime now) {
        TaskType taskType = TaskType.valueOf(task.getTaskTypeCd());
        TaskExecutor taskExecutor = taskExecutors.get(taskType);
        if (taskExecutor == null) {
            throw new IllegalStateException("No TaskExecutor for type: " + taskType);
        }

        TaskResult previousResult = task.getLastExecRslt() == null ? null : TaskResult.valueOf(task.getLastExecRslt());
        TaskExecutionContext context = new TaskExecutionContext(
                task,
                jsonSupport.readTree(task.getExecParamJson()),
                jsonSupport.readTree(task.getSuccessParamJson()),
                triggerType,
                now,
                previousResult
        );

        TaskExecutionResult executionResult;
        try {
            executionResult = taskExecutor.execute(context);
        } catch (Exception e) {
            executionResult = new TaskExecutionResult(
                    TaskResult.ERROR,
                    e.getMessage(),
                    jsonSupport.objectNode().put("error", e.getClass().getSimpleName()),
                    0L,
                    AlertEventType.NONE
            );
        }

        AlertEventType alertEventType = alertPolicyService.determine(previousResult, executionResult.getResult());
        TaskExecutionResult finalResult = executionResult.withAlertEventType(alertEventType);
        saveHistory(task, triggerType, finalResult, now);
        updateTaskSummary(task, finalResult, now);
        notifyIfNeeded(task, previousResult, finalResult, now);
        return finalResult;
    }

    private void saveHistory(MonitoringTask task, ExecutionTriggerType triggerType, TaskExecutionResult result, LocalDateTime now) {
        MonitoringTaskHistory history = new MonitoringTaskHistory();
        history.setTaskHistId(monitoringTaskHistoryRepository.nextHistoryId());
        history.setTaskId(task.getTaskId());
        history.setExecDtm(now);
        history.setExecTrgTypeCd(triggerType.name());
        history.setExecRslt(result.getResult().name());
        history.setExecRsltMsg(result.getMessage());
        history.setExecRsltDataJson(result.getResultData() == null ? "{}" : jsonSupport.write(result.getResultData()));
        history.setAlertEventType(result.getAlertEventType().name());
        history.setExecDurMs(result.getDurationMs());
        history.setFstRegDtm(now);
        history.setFnlUptDtm(now);
        monitoringTaskHistoryRepository.insert(history);
    }

    private void updateTaskSummary(MonitoringTask task, TaskExecutionResult result, LocalDateTime now) {
        monitoringTaskRepository.updateExecutionSummary(
                task.getTaskId(),
                now,
                result.getResult().name(),
                result.getMessage(),
                now
        );
    }

    private void notifyIfNeeded(MonitoringTask task, TaskResult previousResult, TaskExecutionResult result, LocalDateTime now) {
        if (!monitoringProperties.getAlert().isEnabled() || result.getAlertEventType() == AlertEventType.NONE) {
            return;
        }

        AlertEvent alertEvent = new AlertEvent(
                task.getTaskId(),
                task.getTaskNm(),
                previousResult,
                result.getResult(),
                result.getAlertEventType(),
                result.getMessage(),
                now
        );

        if (result.getAlertEventType() == AlertEventType.ALERT_TRIGGERED) {
            alertNotifier.sendAlertTriggered(alertEvent);
        } else if (result.getAlertEventType() == AlertEventType.ALERT_RESOLVED) {
            alertNotifier.sendAlertResolved(alertEvent);
        }
    }
}