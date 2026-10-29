package com.wingbling.logistics.api;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 타 부서가 올리는 발주서가 PDF일 때 쓰는 창구.
 *
 * AI 대신 무료로 처리합니다 — 거래처별로 고정된 PDF 양식을 글자 패턴(정규식)으로
 * 직접 읽어냅니다. 다만 "이미 알고 있는 양식"만 읽을 수 있어서, 새 거래처가 생기거나
 * 기존 거래처가 양식을 바꾸면 이 파일에 그 양식을 읽는 파서를 하나 더 추가해야 합니다.
 *
 * PDF 안의 텍스트는 "표의 틀(라벨)"과 "채워진 값"이 서로 다른 순서로 저장되는 경우가
 * 많고(실제로 같은 거래처 PDF 2장에서도 블록 순서가 서로 달랐습니다), 그래서 줄 번호
 * 같은 절대 위치에 의존하지 않고 고정 문구(랜드마크)를 찾아 그 앞뒤 줄을 읽습니다.
 *
 * 결과는 반드시 사람이 요청서 작성 화면에서 확인한 뒤 평소처럼 "등록" 버튼을 눌러야
 * 실제 요청서가 됩니다 — 여기서는 화면 입력칸을 채워줄 뿐, 등록까지 하지 않습니다.
 */
@RestController
@RequestMapping("/api/order-files")
public class OrderFileController {

    private static final Logger log = LoggerFactory.getLogger(OrderFileController.class);

    // 새 거래처 양식이 생기면 이 목록에 파서를 하나 추가하면 됩니다.
    private final List<OrderPdfTemplate> templates = List.of(new LifestyleProjectTemplate());

    @PostMapping("/parse-pdf")
    public ParseResult parsePdf(@RequestBody ParseDto body) {
        if (body.fileBase64() == null || body.fileBase64().isBlank()) {
            return ParseResult.fail("파일이 비어 있어요.");
        }
        String text;
        try {
            byte[] bytes = Base64.getDecoder().decode(body.fileBase64().trim());
            try (PDDocument doc = PDDocument.load(new ByteArrayInputStream(bytes))) {
                text = new PDFTextStripper().getText(doc);
            }
        } catch (Exception e) {
            log.error("[PDF발주서] PDF 열기 실패", e);
            return ParseResult.fail("PDF 파일을 열지 못했어요. 파일이 손상되지 않았는지 확인해 주세요.");
        }

        for (OrderPdfTemplate t : templates) {
            try {
                ParseResult r = t.tryParse(text);
                if (r != null) return r;
            } catch (Exception e) {
                log.error("[PDF발주서] {} 파서 처리 중 오류", t.getClass().getSimpleName(), e);
            }
        }
        return ParseResult.fail("아직 지원하지 않는 발주서 양식이에요. 직접 입력해 주시거나, 담당자에게 이 양식 추가를 요청해 주세요.");
    }

    // ------------------------------------------------------------------
    // 거래처별 양식 파서 — 새 양식 추가는 이 인터페이스를 구현해서 templates에 등록
    // ------------------------------------------------------------------
    private interface OrderPdfTemplate {
        /** 이 파서가 다루는 양식이면 결과를, 아니면(또는 레이아웃이 달라져 못 믿겠으면) null을 돌려줍니다. */
        ParseResult tryParse(String text);
    }

    /** ㈜ 라이프스타일프로젝트(LSP)가 "주식회사 더블유아일랜드" 앞으로 보내는 "발주서" 양식. */
    private static class LifestyleProjectTemplate implements OrderPdfTemplate {
        private static final Pattern TIME_RANGE = Pattern.compile("\\d{1,2}:\\d{2}\\s*~\\s*\\d{1,2}:\\d{2}.*");
        private static final Pattern NAME_PHONE = Pattern.compile("^(.+?)\\s*\\((01[016789]-?\\d{3,4}-?\\d{4})\\)\\s*$");
        private static final Pattern PRODUCT_ROW = Pattern.compile(
                "^(.+?)\\s+(\\d+)\\s+([\\d,]+|-)\\s+([\\d,]+|-)\\s+([\\d,]+|-)\\s+(\\d{4}-\\d{2}-\\d{2}|-)\\s*(.*)$");
        private static final Pattern HEADER_ROW = Pattern.compile("^발주량\\s");
        private static final Pattern TOTAL_ROW = Pattern.compile("^TOTAL\\b");
        private static final Pattern NUMBERS_ONLY = Pattern.compile("^[\\d,.\\s]+$");

        @Override
        public ParseResult tryParse(String text) {
            if (!text.contains("라이프스타일프로젝트") || !text.contains("발 주 서") || !text.contains("더블유아일랜드")) {
                return null;
            }

            List<String> lines = new ArrayList<>();
            for (String l : text.split("\\r?\\n")) {
                String t = l.trim();
                if (!t.isEmpty()) lines.add(t);
            }

            // ── 입고처 주소 · 입고가능시간 · 입고담당자 ──
            // "아래 기재된 제품에 대하여 발주 의뢰드리오니..." 바로 다음 두 줄이 항상
            // [입고처 주소 + 입고가능시간], [입고담당자 이름(전화번호)] 입니다.
            String address = "", timeNote = "", recvName = "", recvPhone = "";
            int anchor = indexOfContaining(lines, "기재된 제품에 대하여");
            if (anchor >= 0 && anchor + 2 < lines.size()) {
                String addrLine = lines.get(anchor + 1);
                Matcher tm = TIME_RANGE.matcher(addrLine);
                if (tm.find()) {
                    address = addrLine.substring(0, tm.start()).trim();
                    timeNote = addrLine.substring(tm.start()).trim();
                } else {
                    address = addrLine;
                }
                Matcher nm = NAME_PHONE.matcher(lines.get(anchor + 2));
                if (nm.matches()) {
                    recvName = nm.group(1).trim();
                    recvPhone = nm.group(2);
                }
            }
            // 레이아웃이 기대와 다르면(양식이 바뀐 것) 잘못된 값을 채우지 말고 포기합니다.
            if (address.isBlank() || recvName.isBlank()) return null;

            // ── 발주 요청사항(비고) — 입고담당자 줄 다음부터 "상품명" 줄 전까지, 금액 숫자 줄은 제외 ──
            StringBuilder note = new StringBuilder();
            if (!timeNote.isBlank()) note.append(timeNote);
            int stop = indexOfEquals(lines, "상품명");
            if (stop > anchor) {
                for (int i = anchor + 3; i < stop; i++) {
                    String l = lines.get(i);
                    if (NUMBERS_ONLY.matcher(l).matches()) continue;
                    if (note.length() > 0) note.append(" · ");
                    note.append(l);
                }
            }

            // ── 상품 표 — "발주량 ..." 헤더 줄과 "TOTAL" 줄 사이가 상품 줄입니다 ──
            int headerIdx = indexOfMatching(lines, HEADER_ROW);
            int totalIdx = indexOfMatching(lines, TOTAL_ROW);
            List<ProductOut> products = new ArrayList<>();
            String wantDate = "";
            if (headerIdx >= 0 && totalIdx > headerIdx) {
                for (int i = headerIdx + 1; i < totalIdx; i++) {
                    Matcher pm = PRODUCT_ROW.matcher(lines.get(i));
                    if (!pm.matches()) continue;
                    products.add(new ProductOut(pm.group(1).trim(), "", Integer.parseInt(pm.group(2))));
                    if (wantDate.isBlank() && !"-".equals(pm.group(6))) wantDate = pm.group(6);
                }
            }
            if (products.isEmpty()) return null;

            if (!wantDate.isBlank()) {
                if (note.length() > 0) note.append(" · ");
                note.append("입고요청일 ").append(wantDate);
            }

            ReceiverOut receiver = new ReceiverOut(recvName, recvPhone, address, note.toString());
            return new ParseResult(true, receiver, products, null);
        }

        private int indexOfContaining(List<String> lines, String needle) {
            for (int i = 0; i < lines.size(); i++) if (lines.get(i).contains(needle)) return i;
            return -1;
        }

        private int indexOfEquals(List<String> lines, String exact) {
            for (int i = 0; i < lines.size(); i++) if (lines.get(i).equals(exact)) return i;
            return -1;
        }

        private int indexOfMatching(List<String> lines, Pattern p) {
            for (int i = 0; i < lines.size(); i++) if (p.matcher(lines.get(i)).find()) return i;
            return -1;
        }
    }

    public record ParseDto(String fileName, String fileBase64) {}
    public record ReceiverOut(String name, String phone, String address, String message) {}
    public record ProductOut(String name, String option, int qty) {}
    public record ParseResult(boolean ok, ReceiverOut receiver, List<ProductOut> products, String message) {
        static ParseResult fail(String msg) { return new ParseResult(false, null, List.of(), msg); }
    }
}
