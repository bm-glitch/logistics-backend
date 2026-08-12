package com.wingbling.logistics.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface RequestAttachmentRepository extends JpaRepository<RequestAttachment, Long> {

    /**
     * 첨부파일 메타데이터(바이트 제외). 목록/뱃지 표시에 사용합니다.
     * 닫힌 프로젝션이라 SQL이 필요한 컬럼만 조회 → 무거운 data(bytea)는 읽지 않습니다.
     */
    interface Meta {
        Long getId();
        String getSrNo();
        String getFileName();
        String getContentType();
        long getByteSize();
        LocalDateTime getUploadedAt();
    }

    /** 특정 요청(sr)의 첨부 메타 목록 */
    @Query("select a.id as id, a.srNo as srNo, a.fileName as fileName, "
            + "a.contentType as contentType, a.byteSize as byteSize, a.uploadedAt as uploadedAt "
            + "from RequestAttachment a where a.srNo = :srNo order by a.id")
    List<Meta> findMetaBySrNo(@Param("srNo") String srNo);

    /** 전체 첨부 메타 목록 (물류대장에서 한 번에 받아 카드별로 붙임) */
    @Query("select a.id as id, a.srNo as srNo, a.fileName as fileName, "
            + "a.contentType as contentType, a.byteSize as byteSize, a.uploadedAt as uploadedAt "
            + "from RequestAttachment a order by a.srNo, a.id")
    List<Meta> findAllMeta();
}
