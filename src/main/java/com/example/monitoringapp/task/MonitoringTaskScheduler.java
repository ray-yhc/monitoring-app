package com.example.monitoringapp.task;

import com.example.monitoringapp.config.MonitoringProperties;
import com.example.monitoringapp.task.service.MonitoringTaskExecutionService;
import com.example.monitoringapp.task.service.SchedulerExecutionGuard;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class MonitoringTaskScheduler {

    private static final Logger log = LoggerFactory.getLogger(MonitoringTaskScheduler.class);

    private final MonitoringProperties monitoringProperties;
    private final SchedulerExecutionGuard schedulerExecutionGuard;
    private final MonitoringTaskExecutionService monitoringTaskExecutionService;

    public MonitoringTaskScheduler(
            MonitoringProperties monitoringProperties,
            SchedulerExecutionGuard schedulerExecutionGuard,
            MonitoringTaskExecutionService monitoringTaskExecutionService
    ) {
        this.monitoringProperties = monitoringProperties;
        this.schedulerExecutionGuard = schedulerExecutionGuard;
        this.monitoringTaskExecutionService = monitoringTaskExecutionService;
    }

    @Scheduled(cron = "${monitoring.scheduler.cron:0 * * * * *}")
    public void executeScheduledTasks() {
        if (!monitoringProperties.getScheduler().isEnabled()) {
            return;
        }
        if (!schedulerExecutionGuard.tryAcquire()) {
            log.warn("Previous monitoring scheduler execution is still running. Skipping this cycle.");
            return;
        }
        try {
            monitoringTaskExecutionService.executeDueTasks();
        } finally {
            schedulerExecutionGuard.release();
        }
    }
}