package com.example.monitoringapp.report.step;

import com.example.monitoringapp.report.domain.ReportAnalysisContext;
import com.example.monitoringapp.report.domain.TaskExecutionStats;
import com.example.monitoringapp.task.repository.defaultdb.MonitoringTaskHistoryRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ReportDataCollector {

    private final MonitoringTaskHistoryRepository historyRepository;

    public ReportDataCollector(MonitoringTaskHistoryRepository historyRepository) {
        this.historyRepository = historyRepository;
    }

    public void collect(ReportAnalysisContext context) {
        List<TaskExecutionStats> statsList = historyRepository.countStatsByTask(
                context.getAnalysisStartDtm(),
                context.getAnalysisEndDtm()
        );

        int total = 0, success = 0, failure = 0, error = 0;
        for (TaskExecutionStats stats : statsList) {
            total   += stats.getTotalCnt();
            success += stats.getSuccessCnt();
            failure += stats.getFailureCnt();
            error   += stats.getErrorCnt();
        }

        context.setTaskStatsList(statsList);
        context.setTotalExecCnt(total);
        context.setSuccessCnt(success);
        context.setFailureCnt(failure);
        context.setErrorCnt(error);
    }
}
