-- 출고요청 대장 테이블
-- 이사님 정의서 11컬럼 + 물류팀 추가 4컬럼 + 가용재고 스냅샷 3컬럼

CREATE TABLE IF NOT EXISTS shipment_request (
    id              BIGSERIAL PRIMARY KEY,
    sr_no           VARCHAR(20)  NOT NULL UNIQUE,   -- 요청번호 (SR-0001, 자동채번)
    received_at     TIMESTAMP    NOT NULL DEFAULT now(), -- 접수일시 (시스템 자동)

    -- 정의서 원안 필수 필드
    request_team    VARCHAR(50)  NOT NULL,          -- 요청팀
    requester       VARCHAR(50)  NOT NULL,          -- 요청자
    item_name       VARCHAR(100) NOT NULL,          -- 품목
    quantity        INTEGER      NOT NULL,          -- 수량
    want_date       DATE         NOT NULL,          -- 출고 희망일
    receive_place   VARCHAR(100),                   -- 수령처
    note            TEXT,                           -- 비고

    -- 물류팀 추가 필드 (결정필요 #1)
    sku             VARCHAR(50),                    -- 상품코드 (이지어드민 매칭용)
    option_value    VARCHAR(50),                    -- 옵션 (색상/사이즈)
    scope           VARCHAR(10)  NOT NULL DEFAULT 'team', -- 'team'(통합) / 'person'(개인)
    assignee        VARCHAR(50),                    -- 담당자 (scope='person'일 때만)

    -- 상태 관리 (결정필요 #5 — 4단계)
    status          VARCHAR(10)  NOT NULL DEFAULT '대기', -- 대기/진행중/완료/보류
    hold_reason     VARCHAR(200),                   -- 보류 사유 (재고부족/입고지연/요청정보부족/요청팀확인대기/기타)
    completed_at    TIMESTAMP,                      -- 완료일시
    notified_at     TIMESTAMP,                      -- 완료/보류 알림 Slack 발송 시각

    -- 이지어드민 가용재고 스냅샷 (조회 시점 값을 남겨둠 — 감사·재현용)
    stock_phys      INTEGER,                        -- 현재고
    stock_invoice   INTEGER,                        -- 송장 접수건(출고대기)
    stock_pending   INTEGER,                        -- 미처리 접수건(이지오토 전)
    stock_checked_at TIMESTAMP,                     -- 재고 조회 시각

    created_at      TIMESTAMP    NOT NULL DEFAULT now(),
    updated_at      TIMESTAMP    NOT NULL DEFAULT now()
);

CREATE INDEX IF NOT EXISTS idx_shipment_want_date ON shipment_request(want_date);
CREATE INDEX IF NOT EXISTS idx_shipment_status    ON shipment_request(status);
CREATE INDEX IF NOT EXISTS idx_shipment_scope     ON shipment_request(scope);
CREATE INDEX IF NOT EXISTS idx_shipment_sku        ON shipment_request(sku);

COMMENT ON COLUMN shipment_request.status IS '대기 | 진행중 | 완료 | 보류';
COMMENT ON COLUMN shipment_request.scope  IS 'team(물류 통합) | person(개인 요청)';

-- 요청서 첨부파일 (특이사항·참고자료·원본 발주서 등) — v8
CREATE TABLE IF NOT EXISTS request_attachment (
    id           BIGSERIAL    PRIMARY KEY,
    sr_no        VARCHAR(20)  NOT NULL,             -- 어느 요청(SR-xxxx)의 첨부인지
    file_name    VARCHAR(255) NOT NULL,             -- 원본 파일명
    content_type VARCHAR(120),                      -- MIME 타입
    byte_size    BIGINT       NOT NULL DEFAULT 0,   -- 파일 크기(바이트)
    data         BYTEA        NOT NULL,             -- 파일 내용
    uploaded_at  TIMESTAMP    NOT NULL DEFAULT now()
);
CREATE INDEX IF NOT EXISTS idx_attachment_sr_no ON request_attachment(sr_no);

-- 요청 변경 이력 (수정·취소 추적) — v9
CREATE TABLE IF NOT EXISTS request_change_log (
    id          BIGSERIAL   PRIMARY KEY,
    sr_no       VARCHAR(20)  NOT NULL,           -- 어느 요청(SR-xxxx)의 변경인지
    action      VARCHAR(20)  NOT NULL,           -- 수정 | 취소
    actor       VARCHAR(50),                     -- 변경자 이름(화면 입력)
    reason      VARCHAR(500),                    -- 변경 사유
    before_json TEXT,                            -- 변경 전 스냅샷(JSON)
    after_json  TEXT,                            -- 변경 후 스냅샷(JSON)
    changed_at  TIMESTAMP    NOT NULL DEFAULT now()
);
CREATE INDEX IF NOT EXISTS idx_change_log_sr_no ON request_change_log(sr_no);
