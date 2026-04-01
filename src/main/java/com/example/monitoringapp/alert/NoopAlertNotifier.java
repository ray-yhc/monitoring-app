package com.example.monitoringapp.alert;

import com.example.monitoringapp.task.domain.AlertEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class NoopAlertNotifier implements AlertNotifier {

    private static final Logger log = LoggerFactory.getLogger(NoopAlertNotifier.class);

    @Override
    public void sendAlertTriggered(AlertEvent event) {
        log.info("Alert triggered placeholder taskId={}, taskName={}, message={}",
                event.getTaskId(), event.getTaskName(), event.getMessage());
    }

    @Override
    public void sendAlertResolved(AlertEvent event) {
        log.info("Alert resolved placeholder taskId={}, taskName={}, message={}",
                event.getTaskId(), event.getTaskName(), event.getMessage());
    }
}