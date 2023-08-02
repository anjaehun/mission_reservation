package com.example.mission.repository;

import com.example.mission.store.entity.StoreEntity;
import com.example.mission.store.entity.StoreReviewEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StoreReviewRepository extends JpaRepository<StoreReviewEntity, Integer> {
    List<StoreReviewEntity> findByStore(StoreEntity store);

    void deleteByStore(StoreEntity store);
}
