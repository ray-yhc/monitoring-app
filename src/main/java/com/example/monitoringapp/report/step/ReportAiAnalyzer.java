package com.example.monitoringapp.report.step;

import com.example.monitoringapp.report.domain.ReportAnalysisContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Component;

@Component
public class ReportAiAnalyzer {

    private static final Logger log = LoggerFactory.getLogger(ReportAiAnalyzer.class);

    private final ChatClient chatClient;

    public ReportAiAnalyzer(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    public void analyze(ReportAnalysisContext context) {
        if (context.getTotalExecCnt() == 0) {
            context.setAiAdvice("분석 기간 내 실행 이력이 없습니다.");
            return;
        }

        try {
            String advice = chatClient.prompt()
                    .user(context.getPromptContext())
                    .call()
                    .content();
            context.setAiAdvice(advice);
        } catch (Exception e) {
            log.error("AI analysis failed: {}", e.getMessage(), e);
            context.setAiAdvice("AI 분석 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
}
