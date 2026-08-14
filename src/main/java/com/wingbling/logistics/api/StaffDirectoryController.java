package com.wingbling.logistics.api;

import com.wingbling.logistics.domain.StaffSlack;
import com.wingbling.logistics.domain.StaffSlackRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 직원 Slack 주소록 창구.
 *  - GET  /api/staff-slack              : 저장된 주소록 목록(확인용)
 *  - POST /api/staff-slack              : 수동 등록/수정 {requester, slackUserId, slackChannelId}
 *  - POST /api/staff-slack/import-from-slack : Slack users.list로 직원 일괄 가져오기
 */
@RestController
@RequestMapping("/api/staff-slack")
@RequiredArgsConstructor
public class StaffDirectoryController {

    private final StaffSlackRepository repo;
    private final StaffDirectoryService directory;
    private final SlackService slackService;

    @GetMapping
    public List<Map<String, Object>> list() {
        return repo.findAll().stream().map(s -> {
            Map<String, Object> r = new LinkedHashMap<>();
            r.put("name", s.getRequesterName());
            r.put("nameKey", s.getNameKey());
            r.put("slackUserId", s.getSlackUserId());
            return r;
        }).toList();
    }

    @PostMapping
    public Map<String, Object> upsert(@RequestBody SaveDto body) {
        if (body.requester() == null || body.requester().isBlank()
                || body.slackUserId() == null || body.slackUserId().isBlank()) {
            return Map.of("ok", false, "error", "requester/slackUserId 필수");
        }
        directory.remember(body.requester(), body.slackUserId(), body.slackChannelId());
        return Map.of("ok", true);
    }

    @PostMapping("/import-from-slack")
    public Map<String, Object> importFromSlack() {
        int count = slackService.importUsersFromSlack();
        return Map.of("ok", count > 0, "count", count);
    }

    public record SaveDto(String requester, String slackUserId, String slackChannelId) {}
}
