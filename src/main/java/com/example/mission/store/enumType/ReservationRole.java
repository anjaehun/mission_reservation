package com.example.mission.store.enumType;
/*
OK -> 예약 확인
CANCEL -> 예약 취서
HOLD -> 보류
USER_CANCEL -> 유저 단순 변심 -> 취소
TIME_OVER_CANCEL -> 10분 지나서 취소
 */
public enum ReservationRole {
    OK,
    CANCEL,
    TIME_OVER_CANCEL,
    USER_VISIT_CONFIRMATION ,
    PARTNER_USER_CANCEL,
    USER_CANCEL,
    HOLD

}
