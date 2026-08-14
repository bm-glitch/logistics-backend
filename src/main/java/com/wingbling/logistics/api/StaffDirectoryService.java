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

    public StaffSlack lookup(String requester) {
        String key = norm(requester);
        if (key.isEmpty()) return null;
        return repo.findByNameKey(key).orElse(null);
    }
}
