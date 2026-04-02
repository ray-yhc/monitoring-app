# Monitoring App 개발 계획

## 1. 문서 목적
- 본 문서는 `monitoring-app`의 초기 개발 범위, 상세 구현 계획, 산출물 기준을 정리하기 위한 문서다.
- 현재 단계에서는 실제 개발을 시작하지 않고, 구현 착수가 가능한 수준까지 요구사항과 작업 단위를 명확히 정의하는 것을 목표로 한다.
- 본 계획은 이후 추가 요구사항에 따라 보완한다.

## 2. 개발 목표
- 시스템 내 주요 상태를 지속적으로 점검하는 Java 기반 모니터링 애플리케이션을 구축한다.
- 모니터링 대상은 다음을 포함한다.
  - 애플리케이션 health 체크
  - 데이터 정합성 체크
  - URL 응답 체크
  - DB 상태 체크
  - ElasticSearch 로그 조회 결과 체크
  - Prometheus PromQL 조회 결과 체크
- 점검 결과가 기대 조건에 맞지 않을 경우 경보를 발생시키고, 실패 상태에서 성공 상태로 복구되면 경보 해제 알림을 발생시킨다.
- 작업 설정, 이력 조회, 재실행을 웹 UI에서 관리할 수 있도록 한다.
- UI는 Thymeleaf 기반 SSR 방식으로 구현한다.

## 3. 기본 원칙
- 개발계획이 확정되기 전까지는 실제 기능 개발을 시작하지 않는다.
- 작업 및 이력 관리 데이터는 모두 `default db`에 저장한다.
- 작업 실행은 매 분마다 실행 대상 작업을 조회한 뒤 순차 처리한다.
- 스케줄 표현 방식은 1차 구현에서 `cron only`로 고정한다.
- 작업 ID 타입은 1차 구현에서 `bigint`로 고정한다.
- 기존 `MinuteCronTask`, `dbcheck` 패키지는 재사용하지 않는다.
- 알림 채널은 실제 연동하지 않고 placeholder 인터페이스와 빈 구현만 제공한다.
- 운영 화면과 API는 인증을 전체 적용하는 방향으로 설계한다.

## 4. 현재 프로젝트 기준 기술 스택
- Java 17
- Spring Boot 3.x
- Spring Web MVC
- Spring Security
- Spring Actuator
- Spring Scheduler
- MyBatis
- PostgreSQL
- Thymeleaf 추가 예정
- 화면 연동용 REST API 제공

## 5. 요구사항 요약

### 5.1 핵심 동작
1. 매 분마다 실행 대상 작업이 있는지 확인한다.
2. 실행 대상 작업을 순차적으로 수행한다.
3. 각 작업은 실행 파라미터와 성공 조건 파라미터를 기반으로 판정한다.
4. 실행 결과는 이력으로 저장한다.
5. 실패 시 경보 알림, 실패 후 성공 전환 시 경보 해제 알림을 발생시킨다.

### 5.2 관리 기능
- 작업 추가
- 작업 수정
- 작업 삭제
- 작업 조회
- 작업 히스토리 조회
- 작업 재시작

### 5.3 저장소 사용 원칙
- 작업 관리용 API와 화면 관련 데이터는 모두 `default db`만 사용한다.
- 모니터링 대상 DB는 `default`, `module-a`, `module-b`, `module-c` 등 기존 멀티 데이터소스 구성을 활용한다.
- 외부 URL, ElasticSearch, Prometheus 호출 결과도 이력 저장은 모두 `default db`에 기록한다.

## 6. 범위 정의

### 6.1 1차 개발 범위
- 작업 메타정보 관리
- 작업 스케줄 판별 및 순차 실행
- 작업 결과 저장
- 알림 발생 판단 로직
- 알림 발송용 빈 클래스와 메서드 정의
- 작업 목록, 상세, 등록, 수정, 삭제 화면
- 작업 히스토리 화면
- 작업 수동 재실행 기능
- 화면 연동용 API
- URL, DB Query, ES Query, PromQL Query 기반 체크
- 테이블 생성 SQL
- 초기 샘플 데이터 SQL
- API/화면 접근 가이드 문서

### 6.2 1차 개발 제외 범위
- 실제 외부 알림 채널 연동
- 분산 스케줄링
- 다중 인스턴스 분산 락
- 대량 병렬 실행
- 실시간 차트형 대시보드
- 복잡한 권한 체계
- 작업 그룹, 테넌트, 프로젝트 단위 분리

## 7. 아키텍처 개요

### 7.1 논리 구조
- Scheduler Layer
  - 매 분 작업 실행 트리거
- Task Management Layer
  - 작업 CRUD, 조회, 화면 모델 구성
- Execution Layer
  - 작업 타입별 실행기 선택 및 실행
- Alert Layer
  - 경보 발생/해제 판정 및 notifier 호출
- Repository Layer
  - `default db` 작업/이력 저장
- External Integration Layer
  - HTTP 호출, DB 질의, ES 질의, PromQL 질의

### 7.2 실행 흐름
1. `MonitoringTaskScheduler`가 매 분 실행된다.
2. `SchedulerExecutionGuard`가 중복 실행 여부를 확인한다.
3. `MonitoringTaskQueryService`가 `active_yn = 'Y'` 인 작업을 조회한다.
4. `TaskDueEvaluator`가 현재 시각과 cron 식을 비교해 실행 대상을 판별한다.
5. `MonitoringTaskExecutionService`가 `task_prio asc`, `task_id asc` 순으로 순차 실행한다.
6. 각 작업은 `TaskExecutor` 구현체에서 실제 점검을 수행한다.
7. 실행 결과를 `tb_mon_task_hist_l`에 저장한다.
8. `tb_mon_task_m`의 마지막 실행 정보와 결과를 갱신한다.
9. `AlertPolicyService`가 직전 상태와 비교해 경보 발생/해제를 판정한다.
10. `AlertNotifier` placeholder를 호출한다.

### 7.3 중복 실행 방지
- 동일 애플리케이션 인스턴스 내에서는 이전 스케줄 실행이 종료되지 않은 경우 다음 실행을 건너뛴다.
- 1차 구현에서는 단일 인스턴스 기준 메모리 가드로 처리한다.
- 다중 인스턴스 운영 시 분산 락은 후속 범위로 둔다.

## 8. 도메인 모델 설계

### 8.1 주요 도메인
- `MonitoringTask`
  - 작업 정의 엔티티
- `MonitoringTaskHistory`
  - 작업 실행 이력 엔티티
- `TaskExecutionContext`
  - 실행 파라미터, 이전 결과, 실행 트리거, 실행 시각
- `TaskExecutionResult`
  - 성공/실패 여부, 측정 데이터, 메시지, 소요 시간, 알림 이벤트
- `AlertEvent`
  - 알림 없음, 경보 발생, 경보 해제 표현

### 8.2 작업 타입
- `URL_HEALTH_CHECK`
  - health endpoint 호출 후 상태값 점검
- `URL_RESPONSE_CHECK`
  - 일반 URL 응답 코드, 응답 시간, 본문 문자열 점검
- `DB_QUERY_CHECK`
  - DB 쿼리 결과값 점검
- `ES_LOG_CHECK`
  - ElasticSearch `_search` 결과값 점검
- `PROM_QL_CHECK`
  - Prometheus `/api/v1/query` 결과값 점검

## 9. DB 설계

### 9.1 테이블 목록
- `tb_mon_task_m`
  - 작업 마스터
- `tb_mon_task_hist_l`
  - 작업 실행 이력

### 9.2 작업 마스터 테이블
- 테이블명: `tb_mon_task_m`
- 목적: 실행 가능한 작업 정의 저장

#### 컬럼 정의
- `task_id`
  - PK
  - `bigint`
- `task_nm`
  - `varchar(200)`
  - not null
- `task_type_cd`
  - `varchar(50)`
  - not null
- `task_cntnt`
  - `varchar(1000)`
  - null 허용
- `exec_param`
  - `jsonb`
  - not null
- `success_param`
  - `jsonb`
  - not null
- `schedule_val`
  - `varchar(100)`
  - not null
- `task_prio`
  - `integer`
  - default 100
- `active_yn`
  - `char(1)`
  - `Y` 또는 `N`
- `last_exec_dtm`
  - `timestamp`
  - null 허용
- `last_exec_rslt`
  - `varchar(20)`
  - null 허용
- `last_exec_rslt_msg`
  - `varchar(2000)`
  - null 허용
- `fst_reg_dtm`
  - `timestamp`
  - not null
- `fnl_upt_dtm`
  - `timestamp`
  - not null

### 9.3 작업 히스토리 테이블
- 테이블명: `tb_mon_task_hist_l`
- 목적: 작업 실행 결과 추적

#### 컬럼 정의
- `task_hist_id`
  - PK
  - `bigint`
- `task_id`
  - FK to `tb_mon_task_m.task_id`
- `exec_dtm`
  - `timestamp`
  - not null
- `exec_trg_type_cd`
  - `varchar(20)`
  - `SCHEDULER`, `MANUAL`
- `exec_rslt`
  - `varchar(20)`
  - `SUCCESS`, `FAILURE`, `ERROR`
- `exec_rslt_msg`
  - `varchar(2000)`
  - null 허용
- `exec_rslt_data`
  - `jsonb`
  - null 허용
- `alert_event_type`
  - `varchar(30)`
  - `NONE`, `ALERT_TRIGGERED`, `ALERT_RESOLVED`
- `exec_dur_ms`
  - `bigint`
- `fst_reg_dtm`
  - `timestamp`
  - not null
- `fnl_upt_dtm`
  - `timestamp`
  - not null

### 9.4 인덱스 설계
- `tb_mon_task_m(active_yn, schedule_val, task_prio)`
- `tb_mon_task_m(task_type_cd)`
- `tb_mon_task_m(last_exec_rslt)`
- `tb_mon_task_hist_l(task_id, exec_dtm desc)`
- `tb_mon_task_hist_l(exec_dtm desc)`
- `tb_mon_task_hist_l(alert_event_type, exec_dtm desc)`

### 9.5 테이블 생성 SQL 초안
```sql
create table if not exists tb_mon_task_m (
    task_id bigint primary key,
    task_nm varchar(200) not null,
    task_type_cd varchar(50) not null,
    task_cntnt varchar(1000),
    exec_param jsonb not null,
    success_param jsonb not null,
    schedule_val varchar(100) not null,
    task_prio integer not null default 100,
    active_yn char(1) not null default 'Y',
    last_exec_dtm timestamp,
    last_exec_rslt varchar(20),
    last_exec_rslt_msg varchar(2000),
    fst_reg_dtm timestamp not null,
    fnl_upt_dtm timestamp not null,
    constraint ck_tb_mon_task_m_active_yn check (active_yn in ('Y', 'N'))
);

create table if not exists tb_mon_task_hist_l (
    task_hist_id bigint primary key,
    task_id bigint not null,
    exec_dtm timestamp not null,
    exec_trg_type_cd varchar(20) not null,
    exec_rslt varchar(20) not null,
    exec_rslt_msg varchar(2000),
    exec_rslt_data jsonb,
    alert_event_type varchar(30) not null,
    exec_dur_ms bigint not null,
    fst_reg_dtm timestamp not null,
    fnl_upt_dtm timestamp not null,
    constraint fk_tb_mon_task_hist_l_task_id foreign key (task_id)
        references tb_mon_task_m(task_id)
);

create index if not exists idx_tb_mon_task_m_01
    on tb_mon_task_m(active_yn, schedule_val, task_prio);

create index if not exists idx_tb_mon_task_m_02
    on tb_mon_task_m(task_type_cd);

create index if not exists idx_tb_mon_task_m_03
    on tb_mon_task_m(last_exec_rslt);

create index if not exists idx_tb_mon_task_hist_l_01
    on tb_mon_task_hist_l(task_id, exec_dtm desc);

create index if not exists idx_tb_mon_task_hist_l_02
    on tb_mon_task_hist_l(exec_dtm desc);

create index if not exists idx_tb_mon_task_hist_l_03
    on tb_mon_task_hist_l(alert_event_type, exec_dtm desc);
```

### 9.6 초기 데이터 insert 초안
```sql
insert into tb_mon_task_m (
    task_id,
    task_nm,
    task_type_cd,
    task_cntnt,
    exec_param,
    success_param,
    schedule_val,
    task_prio,
    active_yn,
    fst_reg_dtm,
    fnl_upt_dtm
) values (
    1,
    'Local Health Check',
    'URL_HEALTH_CHECK',
    '로컬 애플리케이션 health 체크',
    '{"url":"http://localhost:8080/actuator/health","method":"GET","timeoutMs":3000,"headers":{"Accept":"application/json"}}'::jsonb,
    '{"expectedHttpStatusCode":200,"expectedStatus":"UP"}'::jsonb,
    '0 */1 * * * *',
    10,
    'Y',
    now(),
    now()
);

insert into tb_mon_task_m (
    task_id,
    task_nm,
    task_type_cd,
    task_cntnt,
    exec_param,
    success_param,
    schedule_val,
    task_prio,
    active_yn,
    fst_reg_dtm,
    fnl_upt_dtm
) values (
    2,
    'Module A DB Query Check',
    'DB_QUERY_CHECK',
    'module-a 기본 연결 확인',
    '{"db":"module-a","query":"SELECT 1"}'::jsonb,
    '{"comparison":"EQUALS","expectedValue":1}'::jsonb,
    '0 */1 * * * *',
    20,
    'Y',
    now(),
    now()
);
```

## 10. 파라미터 설계

### 10.1 execution_params 공통 규칙
- JSON 타입으로 저장한다.
- 각 작업 타입별 필수 키를 가진다.
- `timeoutMs`는 없는 경우 시스템 기본값을 사용한다.
- 민감 정보는 평문 저장을 지양하고, 1차 구현에서는 내부 환경 전용으로 사용한다.

### 10.2 execution_params 예시

#### URL_HEALTH_CHECK
```json
{
  "url": "http://localhost:8080/actuator/health",
  "method": "GET",
  "timeoutMs": 3000,
  "headers": {
    "Accept": "application/json"
  }
}
```

#### URL_RESPONSE_CHECK
```json
{
  "url": "https://example.com/api/v1/monitoring-app/status",
  "method": "GET",
  "timeoutMs": 5000,
  "headers": {
    "Accept": "application/json",
    "Authorization": "Bearer ${token}"
  }
}
```

#### DB_QUERY_CHECK
```json
{
  "db": "module-a",
  "query": "SELECT 1"
}
```

```json
{
  "db": "default",
  "query": "SELECT COUNT(*) AS cnt FROM sample_table WHERE status = 'INVALID'"
}
```

#### ES_LOG_CHECK
```json
{
  "url": "http://localhost:9200/logs-*/_search",
  "method": "POST",
  "timeoutMs": 5000,
  "headers": {
    "Content-Type": "application/json"
  },
  "body": {
    "size": 0,
    "query": {
      "bool": {
        "filter": [
          { "match": { "level": "ERROR" } }
        ]
      }
    }
  }
}
```

#### PROM_QL_CHECK
```json
{
  "url": "http://localhost:9090/api/v1/query",
  "method": "GET",
  "timeoutMs": 5000,
  "queryParams": {
    "query": "up{job='monitoring-app'}"
  }
}
```

### 10.3 success_params 예시

#### URL_HEALTH_CHECK
```json
{
  "expectedHttpStatusCode": 200,
  "expectedStatus": "UP"
}
```

#### URL_RESPONSE_CHECK
```json
{
  "expectedHttpStatusCode": 200,
  "maxResponseTimeMs": 3000,
  "containsText": "OK"
}
```

#### DB_QUERY_CHECK
```json
{
  "comparison": "EQUALS",
  "expectedValue": 1
}
```

```json
{
  "comparison": "LESS_THAN",
  "expectedValue": 1
}
```

#### ES_LOG_CHECK
```json
{
  "comparison": "LESS_THAN",
  "expectedValue": 1,
  "valuePath": "hits.total.value"
}
```

#### PROM_QL_CHECK
```json
{
  "comparison": "GREATER_THAN",
  "expectedValue": 0,
  "valuePath": "data.result[0].value[1]"
}
```

### 10.4 타입별 필수 키
- `URL_HEALTH_CHECK`
  - execution: `url`, `method`
  - success: `expectedHttpStatusCode`, `expectedStatus`
- `URL_RESPONSE_CHECK`
  - execution: `url`, `method`
  - success: `expectedHttpStatusCode`
- `DB_QUERY_CHECK`
  - execution: `db`, `query`
  - success: `comparison`, `expectedValue`
- `ES_LOG_CHECK`
  - execution: `url`, `method`, `body`
  - success: `comparison`, `expectedValue`, `valuePath`
- `PROM_QL_CHECK`
  - execution: `url`, `queryParams.query`
  - success: `comparison`, `expectedValue`, `valuePath`

## 11. Enum 설계

### 11.1 필수 Enum
- `TaskType`
  - `URL_HEALTH_CHECK`
  - `URL_RESPONSE_CHECK`
  - `DB_QUERY_CHECK`
  - `ES_LOG_CHECK`
  - `PROM_QL_CHECK`
- `TaskResult`
  - `SUCCESS`
  - `FAILURE`
  - `ERROR`
- `ScheduleType`
  - `CRON`
- `AlertEventType`
  - `NONE`
  - `ALERT_TRIGGERED`
  - `ALERT_RESOLVED`
- `ComparisonType`
  - `EQUALS`
  - `NOT_EQUALS`
  - `GREATER_THAN`
  - `GREATER_THAN_OR_EQUAL`
  - `LESS_THAN`
  - `LESS_THAN_OR_EQUAL`
  - `CONTAINS`
- `ExecutionTriggerType`
  - `SCHEDULER`
  - `MANUAL`
- `UseYn`
  - `Y`
  - `N`

### 11.2 추가 검토 Enum
- `HttpMethodType`
  - `GET`
  - `POST`
- `AuthType`
  - `NONE`
  - `BASIC`
  - `BEARER`

## 12. 패키지 및 클래스 설계

### 12.1 패키지 구성
- `task`
  - 스케줄러, 실행 오케스트레이션, 실행 대상 판별
- `task.rest`
  - REST API 컨트롤러
- `task.web`
  - Thymeleaf 페이지 컨트롤러
- `task.service`
  - 작업 등록, 수정, 삭제, 조회, 실행 서비스
- `task.service.dto`
  - 요청, 응답 DTO
- `task.executor`
  - 작업 타입별 실행기
- `task.repository`
  - default db MyBatis repository
- `task.domain`
  - 도메인, enum, 내부 실행 객체
- `alert`
  - 알림 판단 및 발송 포트
- `common.json`
  - JSON 직렬화, 역직렬화 유틸

### 12.2 예상 클래스
- `MonitoringTaskScheduler`
- `SchedulerExecutionGuard`
- `MonitoringTaskExecutionService`
- `TaskDueEvaluator`
- `TaskExecutor`
- `UrlHealthCheckTaskExecutor`
- `UrlResponseTaskExecutor`
- `DbQueryCheckTaskExecutor`
- `EsLogCheckTaskExecutor`
- `PromQlCheckTaskExecutor`
- `MonitoringTaskService`
- `MonitoringTaskQueryService`
- `MonitoringTaskCommandService`
- `MonitoringTaskRepository`
- `MonitoringTaskHistoryRepository`
- `AlertPolicyService`
- `AlertNotifier`
- `NoopAlertNotifier`
- `MonitoringTaskPageController`
- `MonitoringTaskRestController`
- `MonitoringTaskHistoryRestController`

### 12.3 기존 코드와의 연결 방향
- 기존 `SchedulerConfig`는 재사용한다.
- 기존 `MinuteCronTask`는 참고만 하되 재사용하지 않는다.
- 기존 `dbcheck` 패키지는 재사용하지 않는다.
- 멀티 데이터소스 설정은 현재 구조를 그대로 활용한다.
- 작업 및 이력 관리 repository는 `default db`에만 둔다.

## 13. 작업 타입별 구현 기준

### 13.1 URL_HEALTH_CHECK
- 입력
  - health endpoint URL
  - timeout
  - header
- 판정
  - HTTP 상태 코드 일치
  - JSON `status` 값 일치
- 저장 데이터
  - 응답 코드
  - 응답 시간
  - 응답 status

### 13.2 URL_RESPONSE_CHECK
- 입력
  - URL
  - method
  - timeout
  - header
- 판정
  - 응답 코드
  - 응답 시간
  - 본문 문자열 포함 여부
- 저장 데이터
  - 응답 코드
  - 응답 시간
  - body 일부 요약

### 13.3 DB_QUERY_CHECK
- 입력
  - datasource key
  - SQL
- 판정
  - 단일 스칼라 결과와 비교 조건 일치 여부
- 저장 데이터
  - 실제 조회값
  - 실행 SQL 요약

### 13.4 ES_LOG_CHECK
- 입력
  - `_search` endpoint
  - request body
  - header
- 판정
  - 응답 JSON에서 `valuePath` 값 추출 후 비교
- 저장 데이터
  - 추출값
  - 전체 응답 요약

### 13.5 PROM_QL_CHECK
- 입력
  - `/api/v1/query` endpoint
  - promQL query
- 판정
  - 응답 JSON에서 `valuePath` 값 추출 후 비교
- 저장 데이터
  - 추출값
  - 전체 응답 요약

## 14. 알림 설계

### 14.1 발생 조건
- 경보 알림
  - 현재 실행 결과가 실패이고 성공 파라미터 조건을 만족하지 못한 경우
- 경보 해제 알림
  - 직전 실행 결과가 실패였고 현재 실행 결과가 성공으로 전환된 경우

### 14.2 중복 정책
- 같은 실패 상태가 연속 발생하면 매 실패마다 알림을 발송한다.
- 이 정책은 1차 구현의 명시 결정사항으로 반영한다.

### 14.3 placeholder 인터페이스
```java
public interface AlertNotifier {
    void sendAlertTriggered(AlertEvent event);
    void sendAlertResolved(AlertEvent event);
}
```

### 14.4 기본 구현
- `NoopAlertNotifier`
  - 실제 발송은 하지 않는다.
  - 로그 기록 또는 빈 동작으로 유지한다.

## 15. UI 설계

### 15.1 화면 목록
- 작업 목록 화면
- 작업 등록 화면
- 작업 수정 화면
- 작업 상세 화면
- 작업 히스토리 화면

### 15.2 작업 목록 화면
- 표시 항목
  - 작업 ID
  - 작업명
  - 작업 타입
  - cron
  - 우선순위
  - 활성 여부
  - 마지막 실행 일시
  - 마지막 실행 결과
- 액션
  - 상세
  - 수정
  - 삭제
  - 즉시 실행

### 15.3 작업 등록/수정 화면
- 입력 항목
  - 작업명
  - 작업 타입
  - 작업 설명
  - 실행 파라미터 JSON
  - 성공 파라미터 JSON
  - cron
  - 우선순위
  - 활성 여부
- 편의 기능
  - 작업 타입 선택 시 JSON 예시 template 표시
  - validation 에러 메시지 노출

### 15.4 작업 상세 화면
- 표시 항목
  - 작업 메타정보
  - 최근 실행 결과
  - 최근 이력 10건
  - 최근 알림 이벤트

### 15.5 작업 히스토리 화면
- 표시 항목
  - 실행 일시
  - 실행 트리거
  - 실행 결과
  - 결과 메시지
  - 측정 데이터 요약
  - 알림 이벤트
- 기능
  - 페이징
  - 작업별 조회

## 16. API 설계

### 16.1 공통 원칙
- URI prefix는 `/api/v1/monitoring-app`으로 통일한다.
- 화면 연동 목적의 내부용 API로 설계한다.
- 모든 작업 관리 API는 `default db`만 사용한다.

### 16.2 API 목록
- `GET /api/v1/monitoring-app/tasks`
- `GET /api/v1/monitoring-app/tasks/{taskId}`
- `POST /api/v1/monitoring-app/tasks`
- `PUT /api/v1/monitoring-app/tasks/{taskId}`
- `DELETE /api/v1/monitoring-app/tasks/{taskId}`
- `POST /api/v1/monitoring-app/tasks/{taskId}/restart`
- `GET /api/v1/monitoring-app/tasks/{taskId}/histories`

### 16.3 요청/응답 예시

#### 작업 등록 요청
```json
{
  "taskNm": "Local Health Check",
  "taskTypeCd": "URL_HEALTH_CHECK",
  "taskCntnt": "로컬 health 체크",
  "execParam": {
    "url": "http://localhost:8080/actuator/health",
    "method": "GET",
    "timeoutMs": 3000,
    "headers": {
      "Accept": "application/json"
    }
  },
  "successParam": {
    "expectedHttpStatusCode": 200,
    "expectedStatus": "UP"
  },
  "scheduleVal": "0 */1 * * * *",
  "taskPrio": 10,
  "activeYn": "Y"
}
```

#### 작업 상세 응답
```json
{
  "taskId": 1,
  "taskNm": "Local Health Check",
  "taskTypeCd": "URL_HEALTH_CHECK",
  "taskCntnt": "로컬 health 체크",
  "execParam": {
    "url": "http://localhost:8080/actuator/health",
    "method": "GET",
    "timeoutMs": 3000,
    "headers": {
      "Accept": "application/json"
    }
  },
  "successParam": {
    "expectedHttpStatusCode": 200,
    "expectedStatus": "UP"
  },
  "scheduleVal": "0 */1 * * * *",
  "taskPrio": 10,
  "activeYn": "Y",
  "lastExecDtm": "2026-04-01T10:30:00",
  "lastExecRslt": "SUCCESS",
  "lastExecRsltMsg": "status=UP"
}
```

#### 작업 이력 응답
```json
{
  "taskId": 1,
  "page": 0,
  "size": 20,
  "contents": [
    {
      "taskHistId": 101,
      "execDtm": "2026-04-01T10:30:00",
      "execTrgTypeCd": "SCHEDULER",
      "execRslt": "SUCCESS",
      "execRsltMsg": "status=UP",
      "alertEventType": "NONE",
      "execDurMs": 125,
      "execRsltData": {
        "httpStatusCode": 200,
        "status": "UP"
      }
    }
  ]
}
```

### 16.4 에러 응답 규칙
```json
{
  "code": "INVALID_PARAMETER",
  "message": "execParam.url is required",
  "fieldErrors": [
    {
      "field": "execParam.url",
      "reason": "must not be blank"
    }
  ]
}
```

## 17. 화면 라우트 설계
- `GET /tasks`
- `GET /tasks/new`
- `POST /tasks`
- `GET /tasks/{taskId}`
- `GET /tasks/{taskId}/edit`
- `POST /tasks/{taskId}/edit`
- `POST /tasks/{taskId}/delete`
- `GET /tasks/{taskId}/histories`
- `POST /tasks/{taskId}/restart`

## 18. 검증 및 예외 처리 계획

### 18.1 입력값 검증
- 작업명 필수
- 작업 타입 필수
- 실행 파라미터 JSON 형식 검증
- 성공 파라미터 JSON 형식 검증
- cron 식 유효성 검증
- 우선순위 숫자 범위 검증
- 작업 타입별 필수 파라미터 존재 여부 검증
- URL 체크 시 인증 헤더 및 응답코드 설정 검증

### 18.2 실행 예외 처리
- 개별 작업 실행 중 예외 발생 시 해당 작업은 `ERROR` 또는 `FAILURE`로 기록한다.
- 예외 메시지는 결과 메시지에 저장한다.
- 가능한 경우 요약 응답은 `exec_rslt_data`에 저장한다.
- 다음 작업 실행은 계속 진행한다.

### 18.3 삭제 정책
- 1차 구현에서는 논리 삭제 중심으로 처리한다.
- 실제 삭제 대신 `active_yn = 'N'` 전환을 기본 동작으로 둔다.
- 화면에서는 삭제 버튼을 제공하되 내부 동작은 비활성화 처리로 구현한다.

## 19. 보안 및 운영 계획

### 19.1 인증
- UI/API 전체 인증 적용을 기본 정책으로 둔다.
- local 환경에서는 기존 security 설정을 참고하되, 운영 기준 계획은 보호 기본값으로 설계한다.

### 19.2 민감정보
- Authorization header, query body 등 민감정보는 로그 전체 출력 금지
- 실행 결과 저장 시 민감한 header 값은 마스킹 또는 저장 제외 검토

### 19.3 운영 설정
- 추후 다음 설정을 추가 검토한다.
  - 기본 timeout
  - 수동 실행 허용 여부
  - 스케줄러 활성화 여부
  - 알림 기능 활성화 여부
  - ES 기본 endpoint
  - Prometheus 기본 endpoint

## 20. 테스트 계획

### 20.1 단위 테스트
- cron 스케줄 판별 로직
- 성공 조건 비교 로직
- 경보 발생/해제 판별 로직
- 작업 타입별 실행기 로직
- 응답 JSON `valuePath` 추출 로직
- 예외 발생 시 결과 변환 로직

### 20.2 통합 테스트
- 작업 등록, 수정, 조회, 삭제 API
- 작업 이력 저장
- 분 단위 스케줄 실행 흐름
- 수동 재실행 흐름
- default db 저장과 대상 DB 조회 분리 동작

### 20.3 화면 테스트
- Thymeleaf 렌더링 정상 여부
- 등록/수정 폼 검증 메시지 표시
- 히스토리 페이징 렌더링

## 21. 단계별 개발 계획

### Phase 1. 도메인 및 저장소 설계
- 작업
  - enum 정의
  - domain model 정의
  - repository interface 정의
  - 테이블 DDL 작성
  - 샘플 insert SQL 작성
- 완료 기준
  - 작업/이력 테이블 구조와 컬럼이 확정된다.
  - SQL 초안이 문서화된다.
  - 도메인/DTO 목록이 확정된다.

### Phase 2. 작업 관리 기능
- 작업
  - 작업 CRUD REST API 설계 및 구현
  - Thymeleaf 목록, 등록, 수정, 상세 화면 설계 및 구현
  - 서버 validation 설계
- 완료 기준
  - 화면에서 작업 등록, 수정, 조회, 비활성화가 가능하다.
  - API request/response 계약이 구현 가능 수준으로 고정된다.

### Phase 3. 실행 엔진 구현
- 작업
  - 분 단위 scheduler 구현
  - scheduler 중복 실행 방지 가드 구현
  - cron 기반 due evaluator 구현
  - 순차 실행 orchestration 구현
  - 작업 타입별 executor 인터페이스와 구현체 작성
- 완료 기준
  - 활성 작업 중 due 대상만 순차 실행된다.
  - 실행 결과가 메모리 상에서 일관된 모델로 반환된다.

### Phase 4. 이력 및 알림 처리
- 작업
  - 작업 히스토리 저장
  - 작업 마스터 마지막 실행 상태 갱신
  - 경보 발생/해제 판정
  - placeholder notifier 구현
- 완료 기준
  - 모든 실행에 대해 이력이 남는다.
  - 실패와 복구 시 알림 이벤트가 판정된다.

### Phase 5. 안정화 및 문서화
- 작업
  - 테스트 코드 보강
  - 예외 처리 보강
  - UI/UX 보완
  - README 접속 가이드 작성
- 완료 기준
  - 로컬 기준 실행/검증 절차가 문서화된다.
  - 기본 테스트 케이스가 준비된다.

## 22. 최종 산출물
- `DevPlan.md`
- 테이블 생성 SQL
- 케이스별 초기 데이터 insert SQL
- API 접속 및 화면 접속 가이드 문서 (`README.md`)
- enum 및 DTO 설계 확정안
- 테스트 케이스 목록

## 23. 완료 판단 기준
- 계획 문서만 읽고도 구현 대상, 테이블 구조, API 방향, 화면 범위, 실행 흐름, 테스트 범위를 이해할 수 있어야 한다.
- 작업 타입별 파라미터 구조가 예시 수준을 넘어 구현 가능한 수준으로 정의되어 있어야 한다.
- 단계별 완료 조건이 있어야 하며, 이후 구현 순서가 모호하지 않아야 한다.
- 추가 요구사항이 없더라도 1차 개발 착수가 가능해야 한다.

## 24. 추가 확정 필요 항목
- ES, Prometheus 인증 방식의 표준화 여부
- JSON `valuePath` 문법을 단순 문자열로 둘지 별도 규칙으로 제한할지 여부
- 운영 배포 시 scheduler 단일 인스턴스 보장 방식
- 자유 SQL 허용 범위에 대한 운영 가이드 수준

## 25. 다음 작업 제안
- 본 문서 최종 확정
- 구현 착수 전 DDL 파일 위치 및 패키지 구조 확정
- 이후 실제 개발 시작

## 26. Report Group 연계 기능 추가 계획

### 26.1 목표 및 범위
- 신규 테이블 2개를 추가한다.
  - `tb_mon_report_group_m`
  - `tb_mon_task_report_group_r` (task-report-group 다대다 매핑)
- `tb_mon_report_group_m`는 조회만 제공한다. (생성/수정/삭제 미구현)
- `tb_mon_task_report_group_r`는 Task 생성/수정 화면에서 등록/수정 가능해야 한다.
- 본 단계에서는 구현을 시작하지 않고 설계/계획만 확정한다.

### 26.2 DDL 설계
#### 26.2.1 report group master
```sql
create table if not exists tb_mon_report_group_m (
    report_group_id bigint primary key,
    report_group_name varchar(200) not null,
    description varchar(1000),
    chat_room_id varchar(200) not null,
    send_yn char(1) not null default 'Y',
    fst_reg_dtm timestamp not null,
    fnl_upt_dtm timestamp not null,
    constraint ck_tb_mon_report_group_m_send_yn check (send_yn in ('Y', 'N'))
);

create index if not exists idx_tb_mon_report_group_m_01
    on tb_mon_report_group_m(send_yn);
```

#### 26.2.2 task-report-group relation
```sql
create table if not exists tb_mon_task_report_group_r (
    task_id bigint not null,
    report_group_id bigint not null,
    fst_reg_dtm timestamp not null,
    fnl_upt_dtm timestamp not null,
    constraint pk_tb_mon_task_report_group_r primary key (task_id, report_group_id),
    constraint fk_tb_mon_task_report_group_r_task_id foreign key (task_id)
        references tb_mon_task_m(task_id),
    constraint fk_tb_mon_task_report_group_r_report_group_id foreign key (report_group_id)
        references tb_mon_report_group_m(report_group_id)
);

create index if not exists idx_tb_mon_task_report_group_r_01
    on tb_mon_task_report_group_r(report_group_id, task_id);
```

### 26.3 기능 설계 변경점
- Report Group 조회 기능(읽기 전용)
  - `ReportGroupRepository` (default db, MyBatis)
  - `ReportGroupQueryService`
  - `GET /api/v1/monitoring-app/report-groups`
  - `GET /api/v1/monitoring-app/report-groups/{reportGroupId}`
- Task 생성/수정 확장
  - Task 요청 DTO에 `reportGroupIds: number[]` 추가
  - Task 저장 트랜잭션 내에서 매핑 테이블 동기화
    - 생성: 선택된 그룹 일괄 insert
    - 수정: 기존 매핑 삭제 후 재등록(1차 기본안)
- Task 조회 확장
  - Task 상세/수정 진입 시 연결된 report group 목록 또는 id 목록 조회
  - Task 생성/수정 Thymeleaf 폼에 report group 다중 선택 UI 추가

### 26.4 검증 및 예외 처리
- 저장 시 전달된 `reportGroupIds`가 모두 실존하는지 검증
- 중복 id는 서버에서 unique 처리 후 저장
- 매핑 저장은 Task 저장과 동일 트랜잭션으로 처리
- `send_yn='N'` 그룹 선택 허용 여부는 기본안으로 허용하되, 향후 발송 단계에서 `send_yn` 필터 적용

### 26.5 단계별 구현 계획(사전계획)
1. DDL 스크립트 확정 및 마이그레이션 파일 추가
2. Report Group 조회용 repository/service/API 구현
3. Task DTO/Service에 `reportGroupIds` 반영
4. Task 생성/수정 페이지에 report group 선택 UI 반영
5. Task 상세/조회 응답에 report group 연계 정보 반영
6. 단위/통합/UI 테스트 케이스 추가 및 문서 갱신

### 26.6 확정 필요 항목
- `report_group_id` 채번 전략: bigint sequencial
- `chat_room_id` unique 제약 여부 : X
- Task 목록 화면에서 report group 노출 여부 : X

## 27. 신규 Task Type 추가 계획: DB_TEMPLATE_CHECK

### 27.1 요구사항 요약
- 신규 작업 타입 `DB_TEMPLATE_CHECK`를 추가한다.
- SQL 실행 결과는 템플릿 형태를 따른다.
  - 컬럼: `STATUS`, `MESSAGE`
  - `STATUS` 값: `SUCCESS` 또는 `FAIL`
  - `MESSAGE` 값: 문자열 메시지
- 판정 기준:
  - `STATUS == 'SUCCESS'`이면 성공
  - 그 외(`FAIL`, 기타 값, 출력 포맷 오류)는 실패/에러 처리
- 저장 데이터:
  - 실행 결과 데이터에 `MESSAGE`를 저장한다.
- 출력 포맷 불일치 또는 쿼리 오류 발생 시 에러 메시지를 결과에 기록한다.

### 27.2 범위
- 포함:
  - enum/validator/실행기(executor)/결과 매핑/이력 저장 반영
  - UI(Task Type 선택) 및 JSON 예시 확장
  - API 요청/응답 스키마 반영
- 제외:
  - 기존 타입 동작 변경
  - DB 드라이버/데이터소스 신규 추가

### 27.3 도메인/Enum 변경 계획
- `TaskType`에 `DB_TEMPLATE_CHECK` 추가
- 필요 시 결과 메시지 표준화를 위한 내부 상수 추가
  - 예: `INVALID_TEMPLATE_RESPONSE`, `QUERY_EXECUTION_ERROR`

### 27.4 파라미터/검증 계획
- execution param
  - `db` (datasource key)
  - `query` (SQL)
- success param
  - 없음(또는 빈 객체 허용)
- validator 규칙
  - `db`, `query` 필수
  - SQL 실행 결과에 `STATUS`, `MESSAGE` 컬럼 존재 여부는 런타임 검증

### 27.5 실행 로직 계획
- 신규 executor: `DbTemplateCheckTaskExecutor`
- 처리 순서:
  1. `db`, `query` 추출
  2. 대상 datasource에서 SQL 실행
  3. 첫 행 기준으로 `STATUS`, `MESSAGE` 추출
  4. `STATUS == SUCCESS`면 `TaskResult.SUCCESS`
  5. `STATUS == FAIL`이면 `TaskResult.FAILURE`
  6. 출력 포맷 오류(컬럼 누락/행 없음/타입 불일치)는 `TaskResult.ERROR`
  7. 쿼리 예외 발생 시 `TaskResult.ERROR`
- 저장 데이터(`exec_rslt_data`) 예시
```json
{
  "status": "SUCCESS",
  "message": "template check passed"
}
```
- 오류 시 `exec_rslt_msg`에 원인 요약 기록
  - 예: `Invalid template response: STATUS column missing`
  - 예: `Query execution error: relation ... does not exist`

### 27.6 서비스/리포지토리 영향 계획
- `ExternalDatabaseQueryService` 재사용
- `MonitoringTaskExecutionService`의 executor 매핑에 신규 타입 연결
- 기존 히스토리/마스터 업데이트 로직은 동일 경로 사용

### 27.7 UI/API 반영 계획
- Task 생성/수정 화면
  - Task Type 목록에 `DB_TEMPLATE_CHECK` 추가
  - JSON 예시 추가
    - execution 예시: `{ "db": "default", "query": "select 'SUCCESS' as STATUS, 'ok' as MESSAGE" }`
- REST API
  - 기존 Task create/update 계약에서 `taskTypeCd=DB_TEMPLATE_CHECK` 허용
  - Task detail 응답에서 실행 결과 메시지 확인 가능

### 27.8 예외/경계 케이스 처리 계획
- 결과 행이 0건인 경우: ERROR
- 결과 행이 여러 건인 경우: 1행만 평가(정책 명시)
- `STATUS` 값이 `SUCCESS`/`FAIL` 외 값인 경우: ERROR
- `MESSAGE`가 null인 경우: 빈 문자열로 저장 또는 null 허용(정책 확정 필요)

### 27.9 테스트 계획(추가)
- 단위 테스트
  - STATUS=SUCCESS -> SUCCESS
  - STATUS=FAIL -> FAILURE
  - STATUS 컬럼 누락 -> ERROR
  - MESSAGE 컬럼 누락 -> ERROR
  - 결과 0건 -> ERROR
  - 쿼리 예외 -> ERROR
- 통합 테스트
  - Task 등록 후 수동 실행 시 이력 저장 검증
  - `exec_rslt_msg`, `exec_rslt_data.message` 반영 검증

### 27.10 확정 필요 항목
- `MESSAGE` null 허용 정책 : 가능
- 다건 결과 시 평가 기준(1행 고정 vs 집계 규칙) : 1행 고정
- `STATUS` 대소문자 허용 범위(`SUCCESS`만 허용 vs 대소문자 무시) : 대소문자 무시