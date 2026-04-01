package com.example.monitoringapp;

import com.example.monitoringapp.config.MonitoringProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(MonitoringProperties.class)
public class MonitoringAppApplication {

    public static void main(String[] args) {
        SpringApplication.run(MonitoringAppApplication.class, args);
    }
}