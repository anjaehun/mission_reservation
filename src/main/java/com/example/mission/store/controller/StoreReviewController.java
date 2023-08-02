package com.example.mission.store.controller;

import com.example.mission.store.entity.StoreReviewEntity;
import com.example.mission.store.request.StoreReviewPostRequest;
import com.example.mission.store.service.StoreReviewService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/store")
public class StoreReviewController {

    private final StoreReviewService storeReviewService;

    public StoreReviewController(StoreReviewService storeReviewService) {
        this.storeReviewService = storeReviewService;
    }

    @PostMapping("/comment/{storeId}")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<StoreReviewEntity> postStoreReview(
            @RequestBody StoreReviewPostRequest storeReviewPostRequest,
            @PathVariable Long storeId) {
        StoreReviewEntity storeReview = storeReviewService.postStoreReview(storeReviewPostRequest, storeId);
        return ResponseEntity.ok(storeReview);
    }


}
