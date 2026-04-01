package com.example.monitoringapp.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class MinuteCronTask {

    private static final Logger log = LoggerFactory.getLogger(MinuteCronTask.class);

    @Scheduled(cron = "0 * * * * *")
    public void logEveryMinute() {
        log.info("MinuteCronTask executed at {}", LocalDateTime.now());
    }
}
