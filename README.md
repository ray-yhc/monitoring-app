# monitoring-app

## 개요
- 시스템 health, URL 응답, DB 쿼리, ElasticSearch 로그, PromQL 결과를 작업 단위로 점검하는 Spring Boot 애플리케이션이다.
- 작업과 이력 데이터는 모두 `default db`에 저장한다.

## 주요 경로
- 화면: `/tasks`
- Swagger: `/swagger-ui/index.html`
- Actuator Health: `/actuator/health`

## 로컬 기본 계정
- username: `admin`
- password: `admin1234`

## 사전 준비
1. `application-local.yml` 또는 환경 변수로 DB 접속 정보를 설정한다.
2. `src/main/resources/sql/monitoring-ddl.sql`을 `default db`에 실행한다.
3. 필요 시 `src/main/resources/sql/monitoring-sample-data.sql`을 실행한다.
4. 대상 시스템 URL, ElasticSearch, Prometheus endpoint 접근 가능 여부를 확인한다.

## 실행 방법
1. local profile로 애플리케이션을 실행한다.
2. 브라우저에서 `/tasks`로 접속한다.
3. Basic 인증을 통과한 뒤 작업을 등록하거나 샘플 데이터를 조회한다.

## 테스트 방법
### 1. DDL 적용
- `src/main/resources/sql/monitoring-ddl.sql`을 `default db`에 실행한다.

### 2. 샘플 데이터 적용
- `src/main/resources/sql/monitoring-sample-data.sql`을 필요 시 실행한다.

### 3. 앱 실행
- local profile로 앱을 실행한다.
- Windows 예시: `mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=local`

### 4. 화면 확인
- `/tasks` 접속
- 작업 등록, 수정, 비활성화, 즉시 실행 확인
- `/tasks/{taskId}/histories`에서 이력 확인

### 5. API 확인
- `GET /api/v1/monitoring-app/tasks`
- `POST /api/v1/monitoring-app/tasks`
- `POST /api/v1/monitoring-app/tasks/{taskId}/restart`
- `GET /api/v1/monitoring-app/tasks/{taskId}/histories`

### 6. 스케줄 확인
- 작업의 cron을 `0 * * * * *`로 저장한다.
- 분 단위로 실행 이력이 적재되는지 확인한다.

### 7. 알림 placeholder 확인
- 실패 조건의 작업을 등록한다.
- 실행 후 로그에 placeholder 알림 메시지가 남는지 확인한다.