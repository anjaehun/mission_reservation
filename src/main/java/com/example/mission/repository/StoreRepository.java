package com.example.mission.repository;

import com.example.mission.store.entity.StoreEntity;
import com.example.mission.user.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StoreRepository extends JpaRepository<StoreEntity, Integer> {

    Optional<StoreEntity> findById(Integer storeId);

}
