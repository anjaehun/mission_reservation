package com.example.mission.store.service;

import com.example.mission.repository.StoreReviewRepository;
import com.example.mission.repository.StoreRepository;
import com.example.mission.store.entity.StoreReviewEntity;
import com.example.mission.store.entity.StoreEntity;
import com.example.mission.store.request.StoreReviewPostRequest;
import com.example.mission.user.entity.UserEntity;
import com.example.mission.user.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class StoreReviewService {
    private final StoreRepository storeRepository;
    private final StoreReviewRepository storeReviewRepository;

    private final UserRepository userRepository;

    public StoreReviewService(StoreRepository storeRepository, StoreReviewRepository storeReviewRepository, UserRepository userRepository) {
        this.storeRepository = storeRepository;
        this.storeReviewRepository = storeReviewRepository;
        this.userRepository = userRepository;
    }

    public double calculateAverageRating(long storeId) {
        StoreEntity store = storeRepository.findById((int) storeId)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 매장을 찾을 수 없습니다."));

        // 닉네임이 이미 존재하는지 확인
        List<StoreReviewEntity> reviews = storeReviewRepository.findByStore(store);

        double totalRating = 0;
        double totalReviews = reviews.size();

        for (StoreReviewEntity review : reviews) {
            totalRating += review.getRating();
        }

        double averageRating = totalRating / totalReviews;
        return averageRating;
    }

    /**
     * 댓글 , 별점을 달 수 있음
     * 별점을 달 때 마다 가게의 별점의 평균이 나온다.
     * @param storeReviewPostRequest
     * @param storeId
     * @return
     */
    public StoreReviewEntity postStoreReview(StoreReviewPostRequest storeReviewPostRequest, long storeId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // storeId를 사용하여 StoreEntity 객체 조회
        StoreEntity store = storeRepository.findById((int) storeId)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 매장을 찾을 수 없습니다."));

        String email = "기본 이메일";
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            email = userDetails.getUsername(); // 사용자 이메일 정보를 추출
        }

        // System.out.println("email: " + email);

        String nickname = "기본 닉네임";
        Optional<UserEntity> existingEmail = userRepository.findByEmail(email);

        if (existingEmail.isPresent()) {
            UserEntity user = existingEmail.get();
            System.out.println(user);
            nickname = user.getNickname(); // 사용자의 닉네임을 얻음
        }

        // 닉네임이 이미 존재하는지 확인
       List<StoreReviewEntity> reviews = storeReviewRepository.findByStore(store);

        System.out.println("reviews" + reviews);


        LocalDateTime currentTime = LocalDateTime.now();
        
        var storeReview = StoreReviewEntity.builder()
                .content(storeReviewPostRequest.getComment())
                .rating(storeReviewPostRequest.getRating())
                .nickname(nickname)
                .store(store) // 조회한 StoreEntity 객체를 할당
                .registerDt(currentTime)
                .build();

        storeReviewRepository.save(storeReview);

         // 평균 평점 계산
        double averageRating = calculateAverageRating(storeId);
        System.out.println("averageRating = " +averageRating);
        store.setStar(averageRating);
        storeRepository.save(store);

        return storeReview;

    }


}
