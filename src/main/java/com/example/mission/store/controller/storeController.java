package com.example.mission.store.controller;

import com.example.mission.repository.StoreRepository;
import com.example.mission.store.entity.StoreEntity;
import com.example.mission.store.entity.StoreReviewEntity;
import com.example.mission.store.exception.NoSameAutherException;
import com.example.mission.store.exception.ResourceNotFoundException;
import com.example.mission.store.request.StoreNearRequest;
import com.example.mission.store.request.StorePostRequest;
import com.example.mission.store.service.StoreService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/store")
public class storeController {

    private final StoreService storeService;

    private final StoreRepository storeRepository;

    public storeController(StoreService storeService, StoreRepository storeRepository) {
        this.storeService = storeService;
        this.storeRepository = storeRepository;
    }

    /**
     * 글쓰기 / PARTNER_USER 만 등록 가능
     */
    @PostMapping("/write")
    @PreAuthorize("hasAuthority('PARTNER_USER')") // PARTNER_USER 권한을 가진 사용자만 글을 쓸 수 있음
    public ResponseEntity<StoreEntity> write(@RequestBody StorePostRequest request) {
        // 글 등록 로직을 처리하는 코드
        return ResponseEntity.ok(storeService.write(request));
    }

    /**
     * 리스트 전체 조회
     * @return 전체 리스트
     */
    @GetMapping("/list")
    public ResponseEntity<List<StoreEntity>> getStoreList() {
        List<StoreEntity> storeList = storeService.getAllStores();
        return ResponseEntity.ok(storeList);
    }

    /**
     * 리스트 중 1의 상점 , 리뷰 조회
     *
     * @param storeId
     * @return
     */
    @GetMapping("/list/{storeId}")
    public ResponseEntity<List<StoreReviewEntity>> getStoreById(@PathVariable Long storeId) {
        try {
            List<StoreReviewEntity> reviews = storeService.getStoreAndReviewsById(storeId);
            return ResponseEntity.ok(reviews);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 리스트 중 가나다 순으로 전체 조회
     */
    @GetMapping("/list/sorted")
    public ResponseEntity<List<StoreEntity>> getStoreListSort() {
        List<StoreEntity> storeList = storeService.getAllStoresSorted(); // 변경
        return ResponseEntity.ok(storeList);
    }

    /**
     * 리스트 중 가까운 거리대로 전체 조회
     */
    @PostMapping("/list/near")
    public ResponseEntity<List<StoreEntity>> getStoreListNear(@RequestBody StoreNearRequest request) {
        // request 객체를 사용하여 필요한 데이터를 가져와서 처리합니다.

        System.out.println(request.getXCoordinate());
        System.out.println(request.getYCoordinate());

        List<StoreEntity> storeList = storeService.getAllStoresNear(request.getXCoordinate(),request.getYCoordinate());
        return ResponseEntity.ok(storeList);
    }

    /**
     * 별점 순으로 조회
     * @return
     */
    @GetMapping("/list/starSort")
    public ResponseEntity<List<StoreEntity>> getStoreStarSort() {
        List<StoreEntity> storeList = storeService.getAllStarsSort(); // 변경
        return ResponseEntity.ok(storeList);
    }

    /**
     * 가게 update
     */
    @PutMapping("/update/{storeId}")
    @PreAuthorize("hasAuthority('PARTNER_USER')") // PARTNER_USER 권한을 가진 사용자만 글을 쓸 수 있음
    public ResponseEntity<Object> update(
            @PathVariable Long storeId,
            @RequestBody StorePostRequest request
    ) {
        try {
            StoreEntity updatedStore = (StoreEntity) storeService.update(storeId, request);
            // 응답 데이터 구성
            Map<String, Object> response = new HashMap<>();
            response.put("수정에 성공하였습니다!", "Success"); // 성공 메시지나 상태를 추가로 보낼 수 있음
            response.put("store : ", updatedStore); // 업데이트된 StoreEntity 객체를 응답 데이터에 포함
            return ResponseEntity.ok(response);
        } catch (ResourceNotFoundException e) {
            Map<String, String> response = new HashMap<>();
            response.put("errorMessage", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (NoSameAutherException e) {
            Map<String, String> response = new HashMap<>();
            response.put("errorMessage", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    /**
     * 매장 삭제 1건
     * @param storeId
     * @return
     */
    @DeleteMapping("/delete/{storeId}")
    @PreAuthorize("hasAuthority('PARTNER_USER')")
    public ResponseEntity<Map<String, String>> deleteStore(
            @PathVariable Long storeId
    ) {
        try {
            storeService.deleteStore(storeId);

            // 삭제 성공 메시지를 응답으로 보내기
            Map<String, String> response = new HashMap<>();
            response.put("message", "매장 정보가 성공적으로 삭제되었습니다.");
            return ResponseEntity.ok(response);
        } catch (ResourceNotFoundException e) {
            Map<String, String> response = new HashMap<>();
            response.put("errorMessage", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (NoSameAutherException e) {
            Map<String, String> response = new HashMap<>();
            response.put("errorMessage", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }


}
