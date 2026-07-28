# 출고요청 백엔드 (오늘 밤 뼈대)

이사님 정의서 11컬럼 + 물류팀 추가 필드(옵션/대상/담당자/보류사유) + 이지어드민 가용재고 스냅샷까지
저장하는 최소 백엔드. Spring Boot + PostgreSQL.

## 오늘 밤 목표
- Slack이 나중에 요청을 던질 **접수 창구(POST /api/requests)** 미리 만들어두기
- 대시보드가 데이터를 실제로 읽어올 **조회 API(GET /api/requests)** 만들어두기
- 새로고침해도 안 사라지는 **진짜 저장소(PostgreSQL)** 연결

## 1. 로컬에서 먼저 테스트 (권장)

### 여는 방법
이 폴더를 **IntelliJ로 열면** (`File → Open` → 이 폴더 선택) IntelliJ가 `build.gradle.kts`를 보고
자동으로 Gradle 세팅을 해줘 (gradlew 스크립트 없이도 내장 Gradle로 돌아감). 별도 설치 필요 없음.

### DB 접속정보 넣기
Railway Postgres의 `DATABASE_URL`은 이런 형태로 나와:
```
postgres://USER:PASSWORD@HOST:PORT/railway
```
Spring은 JDBC 형식이 필요해서 아래처럼 변환해서 환경변수로 넣어줘:
```bash
export SPRING_DATASOURCE_URL="jdbc:postgresql://HOST:PORT/railway"
export SPRING_DATASOURCE_USERNAME="USER"
export SPRING_DATASOURCE_PASSWORD="PASSWORD"
```
(HOST/PORT/USER/PASSWORD는 Railway Variables 탭에 있는 DATABASE_URL을 뜯어서 채우기)

### 스키마 적용
Railway Postgres에 접속해서 `schema.sql` 내용을 한 번 실행해줘.
- Railway 대시보드 → Postgres 박스 → "Query" 탭에 schema.sql 내용 붙여넣고 실행
- 또는 로컬에 `psql` 있으면: `psql "$SPRING_DATASOURCE_URL_원본형태" -f schema.sql`

### 실행
IntelliJ에서 `LogisticsApplication.java` 열고 좌측 ▶ 버튼(또는 우클릭 → Run) 누르면 끝.
`http://localhost:8080` 에서 뜸.

(터미널로 하고 싶으면 로컬에 Gradle 있을 때: `gradle bootRun`)

## 2. 동작 확인 (curl)

### 요청 접수 (Slack이 나중에 이 형식으로 보낼 것)
```bash
curl -X POST http://localhost:8080/api/requests \
  -H "Content-Type: application/json" \
  -d '{
    "requestTeam": "마케팅팀",
    "requester": "김서연",
    "itemName": "주얼패치",
    "optionValue": "핑크 / M",
    "quantity": 200,
    "wantDate": "2026-07-25",
    "receivePlace": "부산 팝업스토어",
    "note": "팝업 오픈일 맞춰 오전 도착 필요",
    "sku": "JP-PK-M",
    "scope": "team"
  }'
```

### 목록 조회 (대시보드가 호출할 API)
```bash
curl http://localhost:8080/api/requests
curl http://localhost:8080/api/requests?scope=team
curl http://localhost:8080/api/requests?scope=person
```

### 상태 변경
```bash
curl -X PATCH http://localhost:8080/api/requests/SR-0001/status \
  -H "Content-Type: application/json" -d '{"status":"진행중"}'
```

### 보류 처리
```bash
curl -X PATCH http://localhost:8080/api/requests/SR-0001/hold \
  -H "Content-Type: application/json" -d '{"reason":"재고 부족"}'
```

## 3. Railway에 배포 (내일 이후, 시간 될 때)

1. 이 폴더를 GitHub 새 저장소(`myorder-logistics-backend` 등)에 업로드
2. Railway → New Project → Deploy from GitHub repo → 방금 저장소 선택
3. Railway가 Java/Gradle 프로젝트를 자동 인식해서 빌드함 (Nixpacks)
4. **Variables 탭에서 환경변수 설정** (Postgres 서비스와 자동 연결돼 있으면 `DATABASE_URL`이 자동 제공됨 — 그걸 위 형식대로 변환해서 `SPRING_DATASOURCE_URL` 등으로 다시 세팅)
5. 배포되면 나오는 URL이 백엔드 API 주소. 이걸 프론트(index.html)가 호출하도록 연결

## 4. 내일 할 일 (이지어드민 연동)
- `stock_phys`, `stock_invoice`, `stock_pending` 컬럼에 이지어드민 API 응답을 채워 넣는
  스케줄러 또는 조회 시점 호출 로직 추가
- API 인증키는 **절대 코드에 하드코딩 금지** — Railway Variables에 환경변수로만 저장

## 5. 나중에 (선택)
- 지금은 Spring Data JPA로 빠르게 짰음. 회사에서 쓰는 jOOQ + 헥사고날 구조로
  리팩터링하고 싶으면 도메인 계층은 그대로 두고 리포지토리 구현체만 jOOQ로 교체하면 됨
- 상태 변경 이력(누가 언제 뭘 바꿨는지) 추적하려면 `shipment_request_history` 테이블 추가 고려
