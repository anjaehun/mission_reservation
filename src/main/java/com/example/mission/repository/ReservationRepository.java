package com.example.mission.repository;

import com.example.mission.store.entity.ReservationEntity;
import com.example.mission.store.entity.StoreEntity;
import com.example.mission.store.entity.StoreReviewEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ReservationRepository extends JpaRepository<ReservationEntity, Long> {
    List<ReservationEntity> findByStore(StoreEntity store);

    @Query("SELECT r FROM ReservationEntity r JOIN FETCH r.store s WHERE r.nickname = :userNickname")
    List<ReservationEntity> findByNickname(@Param("userNickname") String userNickname);

    @Query("SELECT r FROM ReservationEntity r JOIN FETCH r.store s WHERE r.reservationAuthor = :reservationAuthor")
    List<ReservationEntity> findByReservationAuthor(@Param("reservationAuthor")String reservationAuthor); //reservationAuthor

    @Modifying
    @Query(value = "UPDATE reservation " +
            "SET reservation_role = 'TIME_OVER_CANCEL' " +
            "WHERE reservation_start_date_time <= :currentTime " +
            "AND DATE_ADD(register_dt,INTERVAL 10 MINUTE) <= :currentTime " +
            "AND reservation_role = 'HOLD'", nativeQuery = true)
    void updateReservationRoleToTimeOverCancel(@Param("currentTime") LocalDateTime currentTime);


    @Query("SELECT r FROM ReservationEntity r JOIN FETCH r.store s WHERE r.reservationId = :reservationId")
    ReservationEntity findByReservationId(@Param("reservationId") Long reservationId);
}
