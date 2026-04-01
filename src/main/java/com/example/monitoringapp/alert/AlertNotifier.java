package com.example.monitoringapp.alert;

import com.example.monitoringapp.task.domain.AlertEvent;

public interface AlertNotifier {

    void sendAlertTriggered(AlertEvent event);

    void sendAlertResolved(AlertEvent event);
}