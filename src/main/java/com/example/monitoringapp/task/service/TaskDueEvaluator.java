package com.example.monitoringapp.task.service;

import com.example.monitoringapp.task.domain.MonitoringTask;
import org.springframework.scheduling.support.CronExpression;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class TaskDueEvaluator {

    public boolean isDue(MonitoringTask task, LocalDateTime now) {
        CronExpression expression = CronExpression.parse(task.getScheduleVal());
        LocalDateTime nextTime = expression.next(now.minusMinutes(1));
        return nextTime != null && !nextTime.isAfter(now);
    }
}