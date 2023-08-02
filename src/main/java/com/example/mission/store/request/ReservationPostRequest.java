package com.example.mission.store.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReservationPostRequest {
    private LocalDateTime reservationStartDateTime;
    private LocalDateTime reservationEndDateTime;
    private Double reservationPay;
    private Double lengthOfStay;


}
