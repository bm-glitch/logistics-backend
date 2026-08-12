package com.wingbling.logistics.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 요청 변경 이력 — 여러 팀이 함께 쓰는 대장부에서 누가·언제·왜·무엇을 바꿨는지 추적하기 위한 기록.
 * 수정(edit)·취소(cancel) 때마다 변경자 이름·사유·변경 전/후 스냅샷을 한 줄로 남깁니다.
 * (로그인이 없어서 '변경자'는 화면에서 입력받은 이름을 그대로 저장합니다.)
 */
@Entity
@Table(name = "request_change_log")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RequestChangeLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "sr_no", nullable = false, length = 20)
    private String srNo;

    /** 수정 | 취소 */
    @Column(name = "action", nullable = false, length = 20)
    private String action;

    /** 변경자 이름 (화면에서 입력받음) */
    @Column(name = "actor", length = 50)
    private String actor;

    /** 변경 사유 */
    @Column(name = "reason", length = 500)
    private String reason;

    /** 변경 전 스냅샷(JSON) */
    @Column(name = "before_json", columnDefinition = "text")
    private String beforeJson;

    /** 변경 후 스냅샷(JSON) */
    @Column(name = "after_json", columnDefinition = "text")
    private String afterJson;

    @Column(name = "changed_at", nullable = false)
    private LocalDateTime changedAt = LocalDateTime.now();

    public static RequestChangeLog of(String srNo, String action, String actor, String reason,
                                      String beforeJson, String afterJson) {
        RequestChangeLog l = new RequestChangeLog();
        l.srNo = srNo;
        l.action = action;
        l.actor = (actor == null || actor.isBlank()) ? "(미입력)" : actor.trim();
        l.reason = (reason == null) ? "" : reason.trim();
        l.beforeJson = beforeJson;
        l.afterJson = afterJson;
        l.changedAt = LocalDateTime.now();
        return l;
    }
}
