package com.wingbling.logistics.api;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

/**
 * 물류팀 전용 화면(재고 감시, A/S 입고 확인 등)의 공용 비밀번호 확인 창구.
 * 타 부서도 접속하는 대시보드라서, 로그인 계정 체계 없이 "물류팀만 아는 비밀번호"
 * 하나로 화면 묶음 전체를 가리는 가벼운 문단속입니다. (강한 보안이 아니라 실수 방지용)
 */
@RestController
@RequestMapping("/api/logistics-gate")
public class LogisticsGateController {

    @Value("${logistics-gate.password:}")
    private String gatePassword;

    @PostMapping("/verify")
    public VerifyResult verify(@RequestBody PasswordDto body) {
        boolean ok = gatePassword != null && !gatePassword.isBlank()
                && gatePassword.equals(body.password());
        return new VerifyResult(ok);
    }

    public record PasswordDto(String password) {}
    public record VerifyResult(boolean ok) {}
}
