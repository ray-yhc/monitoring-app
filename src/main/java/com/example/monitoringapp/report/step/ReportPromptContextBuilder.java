package com.example.monitoringapp.report.step;

import com.example.monitoringapp.report.domain.ReportAnalysisContext;
import com.example.monitoringapp.report.domain.TaskExecutionStats;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;

@Component
public class ReportPromptContextBuilder {

    private static final DateTimeFormatter DTM_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public void build(ReportAnalysisContext context) {
        StringBuilder sb = new StringBuilder();

        sb.append("분석 기간: ")
          .append(context.getAnalysisStartDtm().format(DTM_FMT))
          .append(" ~ ")
          .append(context.getAnalysisEndDtm().format(DTM_FMT))
          .append("\n\n");

        sb.append("[실행 통계 요약]\n");
        sb.append(context.getExecSummary()).append("\n\n");

        sb.append("[작업별 실행 통계]\n");
        for (TaskExecutionStats stats : context.getTaskStatsList()) {
            sb.append(String.format(
                    "- %s (%s)\n  총 %d건 | SUCCESS: %d건 | FAILURE: %d건 | ERROR: %d건\n",
                    stats.getTaskNm(),
                    stats.getTaskTypeCd(),
                    stats.getTotalCnt(),
                    stats.getSuccessCnt(),
                    stats.getFailureCnt(),
                    stats.getErrorCnt()
            ));
        }

        sb.append("\n위 데이터를 바탕으로 이슈가 있는 작업에 대해 심각도와 개선 권고를 작성하세요.");

        context.setPromptContext(sb.toString());
    }
}
