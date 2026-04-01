package com.example.monitoringapp.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class ThirtySecondCronTask {

    private static final Logger log = LoggerFactory.getLogger(ThirtySecondCronTask.class);

    @Scheduled(cron = "*/30 * * * * *")
    public void logEveryThirtySeconds() {
        log.info("ThirtySecondCronTask executed at {}", LocalDateTime.now());
    }
}