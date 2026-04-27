package com.example.monitoringapp.report.service.dto;

import java.time.LocalDate;

public class ReportGenerateRequest {

    private LocalDate targetDt;

    public LocalDate getTargetDt() { return targetDt; }
    public void setTargetDt(LocalDate targetDt) { this.targetDt = targetDt; }
}
