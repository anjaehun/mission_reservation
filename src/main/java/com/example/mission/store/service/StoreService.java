package com.example.mission.store.service;


import com.example.mission.repository.ReservationRepository;
import com.example.mission.repository.StoreRepository;
import com.example.mission.repository.StoreReviewRepository;
import com.example.mission.store.entity.StoreEntity;
import com.example.mission.store.entity.StoreReviewEntity;
import com.example.mission.store.exception.NoSameAutherException;
import com.example.mission.store.exception.ResourceNotFoundException;
import com.example.mission.store.request.StorePostRequest;
import com.example.mission.user.entity.UserEntity;
import com.example.mission.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StoreService {
    private final StoreRepository storeRepository;

    private final UserRepository userRepository;

    private final StoreReviewRepository storeReviewRepository;


    public StoreEntity write(StorePostRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String email;
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            email = userDetails.getUsername(); // 사용자 이메일 정보를 추출
        } else {
            email = "기본 닉네임";
        }

       // System.out.println("email: " + email);

        String nickname = "";
        Optional<UserEntity> existingEmail = userRepository.findByEmail(email);
        if (existingEmail.isPresent()) {
            UserEntity user = existingEmail.get();
            // System.out.println(user);
            nickname = user.getNickname(); // 사용자의 닉네임을 얻음
        }

        System.out.println(nickname);

        LocalDateTime currentTime = LocalDateTime.now(); // 현재 시간 설정

        var store = StoreEntity.builder()
                .storeName(request.getStoreName())
                .location(request.getLocation())
                .explanation(request.getExplanation())
                .xCoordinate(request.getXCoordinate())
                .yCoordinate(request.getYCoordinate())
                .author(nickname)
                .star(0.0)
                .storeImageUrlOne(request.getStoreImageUrlOne())
                .storeImageUrlTwo(request.getStoreImageUrlTwo())
                .storeImageUrlThree(request.getStoreImageUrlThree())
                .registerDt(currentTime)
                .isOpen(request.isOpen())
                .build();
        return storeRepository.save(store);
    }

    public List<StoreEntity> getAllStores() {
        return storeRepository.findAll();
    }


    @Transactional
    public List<StoreReviewEntity> getStoreAndReviewsById(Long storeId) throws ResourceNotFoundException {
        StoreEntity store = storeRepository.findById(Math.toIntExact(storeId))
                .orElseThrow(() -> new ResourceNotFoundException("Store not found with id: " + storeId));

        List<StoreReviewEntity> reviews = storeReviewRepository.findByStore(store);

        return reviews;
    }
    // 가나다 순
    public List<StoreEntity> getAllStoresSorted() {
        List<StoreEntity> storeList = storeRepository.findAll();
        storeList.sort(Comparator.comparing(StoreEntity::getStoreName)); // 가나다 순으로 정렬
        return storeList;
    }

    // 가까운 매장 순
    public List<StoreEntity> getAllStoresNear(double xCoordinate, double yCoordinate) {
        List<StoreEntity> storeList = storeRepository.findAll();
        storeList.sort(Comparator.comparing(StoreEntity::getStoreName)); // 가나다 순으로 정렬

        // 거리 기준으로 정렬
        storeList.sort(Comparator.comparing(store
                -> calculateDistance(store.getXCoordinate(), store.getYCoordinate(), xCoordinate, yCoordinate)));

        return storeList;
    }

    // Haversine 공식을 이용하여 두 지점 간의 거리 계산
    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // 지구 반지름 (단위: km)

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);

        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return R * c;
    }

    public List<StoreEntity> getAllStarsSort() {
        List<StoreEntity> storeList = storeRepository.findAll();
        storeList.sort(Comparator.comparingDouble(StoreEntity::getStar).reversed()); // star를 내림차순으로 정렬

        return storeList;
    }

    public Object update(Long storeId, StorePostRequest request) throws ResourceNotFoundException, NoSameAutherException {

        StoreEntity storeEntity = storeRepository.findById(Math.toIntExact(storeId))
                .orElseThrow(() -> new ResourceNotFoundException("Store not found with id: " + storeId));


        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String email;
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            email = userDetails.getUsername(); // 사용자 이메일 정보를 추출
        } else {
            email = "기본 닉네임";
        }

        // System.out.println("email: " + email);

        String nickname = "";
        Optional<UserEntity> existingEmail = userRepository.findByEmail(email);
        if (existingEmail.isPresent()) {
            UserEntity user = existingEmail.get();
            // System.out.println(user);
            nickname = user.getNickname(); // 사용자의 닉네임을 얻음
        }

        LocalDateTime currentTime = LocalDateTime.now(); // 현재 시간 설정

        if (!storeEntity.getAuthor().equals(nickname)) {
            throw new NoSameAutherException("같은 닉네임이 아닙니다.");
        }

            storeEntity.setStoreName(request.getStoreName());
            storeEntity.setLocation(request.getLocation());
            storeEntity.setExplanation(request.getExplanation());
            storeEntity.setXCoordinate(request.getXCoordinate());
            storeEntity.setYCoordinate(request.getYCoordinate());
            storeEntity.setModifiedDt(currentTime);
            storeEntity.setIsOpen(request.isOpen());

        return storeRepository.save(storeEntity);
    }

    @Transactional
    public StoreEntity deleteStore(Long storeId) throws ResourceNotFoundException, NoSameAutherException {
        StoreEntity storeEntity = storeRepository.findById(Math.toIntExact(storeId))
                .orElseThrow(() -> new ResourceNotFoundException(storeId + "번 글이 존재하지 않습니다."));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String email;
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            email = userDetails.getUsername(); // 사용자 이메일 정보를 추출
        } else {
            email = "기본 닉네임";
        }

        // System.out.println("email: " + email);

        String nickname = "";
        Optional<UserEntity> existingEmail = userRepository.findByEmail(email);
        if (existingEmail.isPresent()) {
            UserEntity user = existingEmail.get();
            // System.out.println(user);
            nickname = user.getNickname(); // 사용자의 닉네임을 얻음
        }

        LocalDateTime currentTime = LocalDateTime.now(); // 현재 시간 설정

        if (!storeEntity.getAuthor().equals(nickname)) {
            throw new NoSameAutherException("같은 닉네임이 아닙니다.");
        }

        storeReviewRepository.deleteByStore(storeEntity);
        storeRepository.delete(storeEntity);

        return storeEntity;
    }


}
