package com.wingbling.logistics.api;

import com.wingbling.logistics.domain.StoreAddress;
import com.wingbling.logistics.domain.StoreAddressRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 매장 주소록 창구 — 대시보드의 "매장 관리" 화면이 이 API로 읽고 씁니다.
 * 예전엔 매장이 추가되거나 주소가 바뀔 때마다 코드(index.html)를 고쳐서 배포해야 했는데,
 * 이제 여기서 바로 추가·수정·삭제하면 다음 화면 새로고침부터 바로 반영됩니다.
 */
@RestController
@RequestMapping("/api/store-addresses")
@RequiredArgsConstructor
@Transactional
public class StoreAddressController {

    private final StoreAddressRepository repo;

    @GetMapping
    public List<StoreAddress> list() {
        return repo.findAll(org.springframework.data.domain.Sort.by("name"));
    }

    @PostMapping
    public StoreAddress create(@RequestBody SaveDto body) {
        StoreAddress s = new StoreAddress();
        apply(s, body);
        return repo.save(s);
    }

    @PutMapping("/{id}")
    public StoreAddress update(@PathVariable Long id, @RequestBody SaveDto body) {
        StoreAddress s = repo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("매장을 찾을 수 없어요: " + id));
        apply(s, body);
        return repo.save(s);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        repo.deleteById(id);
    }

    private void apply(StoreAddress s, SaveDto body) {
        s.setName(body.name() == null ? "" : body.name().trim());
        s.setAddr(body.addr());
        s.setPhone(body.phone());
        s.setAliases(body.aliases());
    }

    public record SaveDto(String name, String addr, String phone, String aliases) {}
}
