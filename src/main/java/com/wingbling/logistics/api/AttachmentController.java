package com.wingbling.logistics.api;

import com.wingbling.logistics.domain.RequestAttachment;
import com.wingbling.logistics.domain.RequestAttachmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * 요청서 첨부파일 창구.
 *  - POST   /api/attachments/{sr}            : 파일 업로드(여러 개). 요청서(sr) 생성 후 호출.
 *  - GET    /api/attachments/by-request/{sr} : 특정 요청의 첨부 목록(메타데이터)
 *  - GET    /api/attachments/all             : 전체 첨부 목록(메타데이터) — 물류대장에서 한 번에 사용
 *  - GET    /api/attachments/{id}/download   : 첨부 다운로드(실제 파일)
 *  - DELETE /api/attachments/{id}            : 첨부 삭제
 */
@RestController
@RequestMapping("/api/attachments")
@RequiredArgsConstructor
public class AttachmentController {

    private final RequestAttachmentRepository repo;

    /** 프론트로 내려주는 첨부 메타데이터(파일 내용은 제외) */
    public record AttachmentDto(Long id, String srNo, String fileName,
                                String contentType, long byteSize, String uploadedAt) {
        static AttachmentDto from(RequestAttachmentRepository.Meta m) {
            return new AttachmentDto(m.getId(), m.getSrNo(), m.getFileName(),
                    m.getContentType(), m.getByteSize(),
                    m.getUploadedAt() == null ? "" : m.getUploadedAt().toString());
        }
        static AttachmentDto from(RequestAttachment a) {
            return new AttachmentDto(a.getId(), a.getSrNo(), a.getFileName(),
                    a.getContentType(), a.getByteSize(),
                    a.getUploadedAt() == null ? "" : a.getUploadedAt().toString());
        }
    }

    /** 첨부 업로드 (여러 개). multipart 필드 이름은 "files". */
    @PostMapping("/{sr}")
    public List<AttachmentDto> upload(@PathVariable("sr") String sr,
                                      @RequestParam("files") List<MultipartFile> files) throws IOException {
        List<AttachmentDto> out = new ArrayList<>();
        if (files != null) {
            for (MultipartFile f : files) {
                if (f == null || f.isEmpty()) continue;
                RequestAttachment saved = repo.save(
                        RequestAttachment.of(sr, f.getOriginalFilename(), f.getContentType(), f.getBytes()));
                out.add(AttachmentDto.from(saved));
            }
        }
        return out;
    }

    /** 특정 요청(sr)의 첨부 목록 */
    @GetMapping("/by-request/{sr}")
    public List<AttachmentDto> listByRequest(@PathVariable("sr") String sr) {
        return repo.findMetaBySrNo(sr).stream().map(AttachmentDto::from).toList();
    }

    /** 전체 첨부 목록 (메타데이터만) */
    @GetMapping("/all")
    public List<AttachmentDto> listAll() {
        return repo.findAllMeta().stream().map(AttachmentDto::from).toList();
    }

    /** 첨부 다운로드 — 실제 파일 바이트를 내려줍니다. */
    @GetMapping("/{id}/download")
    public ResponseEntity<ByteArrayResource> download(@PathVariable("id") Long id) {
        RequestAttachment a = repo.findById(id).orElse(null);
        if (a == null || a.getData() == null) {
            return ResponseEntity.notFound().build();
        }
        String fileName = (a.getFileName() == null || a.getFileName().isBlank()) ? "attachment" : a.getFileName();
        String encoded = URLEncoder.encode(fileName, StandardCharsets.UTF_8).replace("+", "%20");

        MediaType mediaType;
        try {
            mediaType = (a.getContentType() == null || a.getContentType().isBlank())
                    ? MediaType.APPLICATION_OCTET_STREAM
                    : MediaType.parseMediaType(a.getContentType());
        } catch (Exception e) {
            mediaType = MediaType.APPLICATION_OCTET_STREAM;
        }

        return ResponseEntity.ok()
                .contentType(mediaType)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + encoded)
                .contentLength(a.getByteSize())
                .body(new ByteArrayResource(a.getData()));
    }

    /** 첨부 삭제 */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        if (repo.existsById(id)) repo.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
