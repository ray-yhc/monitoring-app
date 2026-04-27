package com.example.monitoringapp.report.step;

import com.example.monitoringapp.report.domain.ReportAnalysisContext;
import org.springframework.stereotype.Component;

@Component
public class ReportSummaryBuilder {

    public void build(ReportAnalysisContext context) {
        int total   = context.getTotalExecCnt();
        int success = context.getSuccessCnt();
        int failure = context.getFailureCnt();
        int error   = context.getErrorCnt();

        String successRate = total > 0
                ? String.format("%.1f%%", success * 100.0 / total)
                : "N/A";

        String summary = String.format(
                "실행이력: 총 %,d건 (SUCCESS: %,d건, FAILURE: %,d건, ERROR: %,d건, 성공률 %s)",
                total, success, failure, error, successRate
        );

        context.setExecSummary(summary);
    }
}
