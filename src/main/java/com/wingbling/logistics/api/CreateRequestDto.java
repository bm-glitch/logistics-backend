package com.wingbling.logistics.api;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;

/**
 * 요청 접수 DTO.
 * 나중에 대표님 Slack 봇이 이 형식(또는 비슷한 형식)으로 POST 하게 될 자리.
 * 지금은 수동 테스트(Postman/curl)로 이 형식을 그대로 넣어서 확인하면 됨.
 */
public record CreateRequestDto(
        @NotBlank String requestTeam,     // 요청팀
        @NotBlank String requester,       // 요청자
        @NotBlank String itemName,        // 품목
        String optionValue,               // 옵션 (색상/사이즈) — 선택
        @NotNull @Positive Integer quantity, // 수량
        @NotNull LocalDate wantDate,       // 출고 희망일
        String receivePlace,               // 수령처
        String note,                       // 비고
        String sku,                        // 상품코드 (이지어드민 매칭용) — 선택
        String scope,                       // "team" | "person" — 미지정시 team
        String assignee                     // scope=person일 때 담당자명
) {}
