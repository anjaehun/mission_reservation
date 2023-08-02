package com.example.mission.store.entity;

import com.example.mission.store.enumType.MoneySuccessRole;
import com.example.mission.store.enumType.ReservationRole;
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
@Table(name = "reservation")
public class ReservationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reservationId;

    @ManyToOne
    @JoinColumn(name = "store_id")
    private StoreEntity store;

    @Column
    private String nickname;

    // 예약 상태
    @Enumerated(EnumType.STRING)
    private ReservationRole reservationRole;

    // 예약 시작 일자
    @Column
    private LocalDateTime reservationStartDateTime;

    // 예약 종료 일자
    @Column
    private LocalDateTime  reservationEndDateTime;



    @Enumerated(EnumType.STRING)
    private MoneySuccessRole moneySuccessRole;

    // 예약 결제 금액 결과
    @Column
    private Double reservationPay;


    @Column
    private String reservationAuthor;

    // 예약 일수
    @Column
    private Double lengthOfStay;


    @Column
    private LocalDateTime registerDt;


}
