package com.wingbling.logistics.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 직원 Slack 주소록 — "요청자 이름 → Slack 사용자ID".
 * 대장부/모바일로 요청서를 써도, 요청자 이름이 여기에 있으면 Slack ID를 붙여
 * 송장/완료 알림이 그 사람 개인 DM으로 가게 합니다.
 */
@Entity
@Table(name = "staff_slack")
@Getter
@Setter
@NoArgsConstructor
public class StaffSlack {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 요청자 이름 정규화 키(공백 제거·소문자) — 매칭에 사용 */
    @Column(name = "name_key", nullable = false, unique = true, length = 200)
    private String nameKey;

    /** 원본 이름(표시용) */
    @Column(name = "requester_name", length = 200)
    private String requesterName;

    @Column(name = "slack_user_id", nullable = false, length = 20)
    private String slackUserId;

    @Column(name = "slack_channel_id", length = 20)
    private String slackChannelId;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();
}
