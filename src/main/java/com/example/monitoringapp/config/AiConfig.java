package com.example.monitoringapp.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AiConfig {

    @Bean
    public ChatClient reportChatClient(ChatClient.Builder builder) {
        return builder
                .defaultSystem("""
                        당신은 IT 시스템 모니터링 전문가입니다.
                        제공된 모니터링 이력 데이터를 분석하여 운영자를 위한 실용적인 한국어 조언을 작성하세요.
                        각 이슈 항목 앞에 심각도([심각] / [주의] / [일반])를 직접 판단하여 표시하세요.
                        심각도 판단 기준: 발생 빈도, 연속성, 시스템 영향도를 종합적으로 고려하세요.
                        - ERROR: 작업 실행 자체 실패 (연결 오류, 예외 발생 등)
                        - FAILURE: 작업은 실행되었으나 성공 조건 불충족 (임계치 초과, 값 불일치 등)
                        이슈가 있는 작업만 번호 목록으로 작성하고, 이슈 없는 작업은 생략하세요.
                        모든 작업이 정상이라면 "지난 24시간 내 특이사항이 없습니다." 라고만 답변하세요.
                        """)
                .build();
    }
}
