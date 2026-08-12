package com.wingbling.logistics.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 요청서 첨부파일. 요청자가 요청서 작성 시 올린 파일(특이사항·참고자료·원본 발주서 등)을
 * 서버(Postgres)에 그대로 보관해서, 물류팀이 나중에 열어볼 수 있게 합니다.
 */
@Entity
@Table(name = "request_attachment")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RequestAttachment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "sr_no", nullable = false, length = 20)
    private String srNo;

    @Column(name = "file_name", nullable = false, length = 255)
    private String fileName;

    @Column(name = "content_type", length = 120)
    private String contentType;

    @Column(name = "byte_size", nullable = false)
    private long byteSize;

    @Column(name = "data", nullable = false, columnDefinition = "bytea")
    private byte[] data;

    @Column(name = "uploaded_at", nullable = false)
    private LocalDateTime uploadedAt = LocalDateTime.now();

    public static RequestAttachment of(String srNo, String fileName, String contentType, byte[] data) {
        RequestAttachment a = new RequestAttachment();
        a.srNo = srNo;
        a.fileName = (fileName == null || fileName.isBlank()) ? "첨부파일" : fileName;
        a.contentType = contentType;
        a.data = data;
        a.byteSize = data == null ? 0 : data.length;
        a.uploadedAt = LocalDateTime.now();
        return a;
    }
}
