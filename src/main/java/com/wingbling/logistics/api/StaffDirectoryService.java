package com.wingbling.logistics.api;

import com.wingbling.logistics.domain.StaffSlack;
import com.wingbling.logistics.domain.StaffSlackRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 직원 Slack 주소록 서비스.
 *  - remember(): Slack으로 들어온 요청에서 "이름 → SlackID"를 자동 저장(있으면 갱신)
 *  - lookup()  : 대장부/모바일 요청 시 이름으로 SlackID를 찾음
 */
@Service
@RequiredArgsConstructor
@Transactional
public class StaffDirectoryService {

    private final StaffSlackRepository repo;

    /** 이름 정규화 키(공백 제거 + 소문자). 매칭 기준을 통일합니다. */
    public static String norm(String name) {
        if (name == null) return "";
        return name.replaceAll("\\s+", "").toLowerCase().trim();
    }

    public void remember(String requester, String slackUserId, String slackChannelId) {
        if (requester == null || requester.isBlank() || slackUserId == null || slackUserId.isBlank()) return;
        String key = norm(requester);
        if (key.isEmpty()) return;
        StaffSlack s = repo.findByNameKey(key).orElseGet(StaffSlack::new);
        s.setNameKey(key);
        s.setRequesterName(requester.trim());
        s.setSlackUserId(slackUserId.trim());
        if (slackChannelId != null && !slackChannelId.isBlank()) s.setSlackChannelId(slackChannelId.trim());
        s.setUpdatedAt(LocalDateTime.now());
        repo.save(s);
    }

    /** 요청 접수 시 자동 학습 전용 — 이미 등록된 이름이면 절대 덮어쓰지 않습니다.
     *  ⚠ 실제 사고: 다른 사람이 이름(예: "이호태")을 대신 접수해주면, 그 순간 접속해 있던
     *  대신 접수한 사람의 Slack ID로 그 이름의 주소록이 통째로 바뀌어버렸습니다(remember()가
     *  무조건 덮어썼기 때문). 그 뒤로 그 이름 앞으로 가는 모든 알림이 엉뚱한 사람에게 갔습니다.
     *  그래서 자동 학습은 "이름이 주소록에 아직 없을 때"만 등록하고, 이미 있는 이름은
     *  절대 자동으로 손대지 않습니다. 정정이 필요하면 관리자가 /api/staff-slack 로 직접 고치거나
     *  Slack 공식 명단 가져오기(신뢰된 소스, remember() 그대로 사용)로만 갱신합니다. */
    public void rememberIfAbsent(String requester, String slackUserId, String slackChannelId) {
        if (requester == null || requester.isBlank() || slackUserId == null || slackUserId.isBlank()) return;
        String key = norm(requester);
        if (key.isEmpty()) return;
        if (repo.findByNameKey(key).isPresent()) return;   // 이미 등록된 이름은 자동으로 건드리지 않음
        remember(requester, slackUserId, slackChannelId);
    }

    public StaffSlack lookup(String requester) {
        String key = norm(requester);
        if (key.isEmpty()) return null;
        return repo.findByNameKey(key).orElse(null);
    }
}
