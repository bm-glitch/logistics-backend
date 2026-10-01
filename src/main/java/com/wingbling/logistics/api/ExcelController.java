package com.wingbling.logistics.api;

import org.apache.poi.poifs.crypt.Decryptor;
import org.apache.poi.poifs.crypt.EncryptionInfo;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Base64;

/**
 * 거래처가 비밀번호로 암호화해서 보내는 엑셀 파일을 복호화하는 창구.
 *
 * 브라우저(xlsx.js)는 암호 걸린 엑셀을 못 읽기 때문에, 파일을 그대로 서버로 보내면
 * 여기서 Apache POI로 암호를 풀어 평문 엑셀 바이트를 돌려줍니다. 그 결과를 다시
 * 프론트에서 xlsx.js로 읽으면 기존 양식 인식 로직을 그대로 쓸 수 있습니다.
 *
 * 예시: POST /api/excel/decrypt {"fileBase64":"...","password":"1234"}
 * 응답(성공): {"ok":true,"fileBase64":"..."}
 * 응답(실패): {"ok":false,"message":"비밀번호가 올바르지 않아요."}
 */
@RestController
@RequestMapping("/api/excel")
public class ExcelController {

    @PostMapping("/decrypt")
    public DecryptResult decrypt(@RequestBody DecryptDto dto) {
        if (dto.fileBase64() == null || dto.fileBase64().isBlank()) {
            return new DecryptResult(false, null, "파일이 비어 있어요.");
        }
        if (dto.password() == null || dto.password().isBlank()) {
            return new DecryptResult(false, null, "비밀번호를 입력해 주세요.");
        }
        byte[] raw;
        try {
            raw = Base64.getDecoder().decode(dto.fileBase64().trim());
        } catch (Exception e) {
            return new DecryptResult(false, null, "파일을 읽을 수 없어요.");
        }
        try (POIFSFileSystem fs = new POIFSFileSystem(new ByteArrayInputStream(raw))) {
            EncryptionInfo info = new EncryptionInfo(fs);
            Decryptor decryptor = Decryptor.getInstance(info);
            if (!decryptor.verifyPassword(dto.password())) {
                return new DecryptResult(false, null, "비밀번호가 올바르지 않아요.");
            }
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            try (InputStream in = decryptor.getDataStream(fs)) {
                in.transferTo(out);
            }
            return new DecryptResult(true, Base64.getEncoder().encodeToString(out.toByteArray()), null);
        } catch (Exception e) {
            return new DecryptResult(false, null, "비밀번호로 잠긴 엑셀 파일이 맞는지 확인해 주세요.");
        }
    }

    public record DecryptDto(String fileBase64, String password) {}

    public record DecryptResult(boolean ok, String fileBase64, String message) {}
}
