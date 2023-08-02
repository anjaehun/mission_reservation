package com.example.mission.store.entity;

import com.example.mission.user.entity.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "storeReview")
public class StoreReviewEntity {

    // review id
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reviewId;

    // store_id join
    @ManyToOne
    @JoinColumn(name = "store_id")
    private StoreEntity store;

    // 예약 요청 사항
    @Column(columnDefinition = "TEXT")
    private String content;

    @Column
    private Double rating;

    @Column
    private String nickname;

    // 예약
    @Column
    private LocalDateTime registerDt;
}
