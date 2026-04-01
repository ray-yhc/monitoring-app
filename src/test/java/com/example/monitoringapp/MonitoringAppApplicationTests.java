package com.example.monitoringapp;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@Disabled("Requires local DB setup and is not executed automatically in this task")
@ActiveProfiles("local")
@SpringBootTest
class MonitoringAppApplicationTests {

    @Test
    void contextLoads() {
    }
}