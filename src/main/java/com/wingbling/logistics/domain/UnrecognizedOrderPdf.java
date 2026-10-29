package com.wingbling.logistics.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * "모르는 양식 자동 수집함" — PDF 발주서를 올렸는데 아는 양식이 아니어서 못 읽었을 때,
 * 그냥 실패로 끝내지 않고 여기에 쌓아 둡니다. 나중에 "쌓인 거 처리해줘" 한 번이면
 * 담당 개발자가 이 목록을 보고 몰아서 새 양식 파서를 추가할 수 있습니다.
 */
@Entity
@Table(name = "unrecognized_order_pdf")
@Getter
@Setter
@NoArgsConstructor
public class UnrecognizedOrderPdf {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "file_name", length = 300)
    private String fileName;

    @Column(name = "file_base64", columnDefinition = "text")
    private String fileBase64;

    @Column(name = "extracted_text", columnDefinition = "text")
    private String extractedText;

    @Column(name = "uploaded_at", nullable = false)
    private LocalDateTime uploadedAt = LocalDateTime.now();

    @Column(name = "resolved", nullable = false)
    private boolean resolved = false;
}
