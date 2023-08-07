package com.example.mission.store.service;

import com.example.mission.repository.ReservationRepository;
import com.example.mission.repository.StoreRepository;
import com.example.mission.store.entity.ReservationEntity;
import com.example.mission.store.entity.StoreEntity;
import com.example.mission.store.enumType.MoneySuccessRole;
import com.example.mission.store.enumType.ReservationRole;
import com.example.mission.store.exception.NoSameAutherException;

import com.example.mission.store.exception.RateTimeException;
import com.example.mission.store.exception.ReservationDatePassedException;
import com.example.mission.store.request.ReservationPostRequest;
import com.example.mission.user.entity.UserEntity;
import com.example.mission.user.repository.UserRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ReservationService {

    private final StoreRepository storeRepository;
    private final UserRepository userRepository;

    private final ReservationRepository reservationRepository;


    public ReservationService(StoreRepository storeRepository, UserRepository userRepository, ReservationRepository reservationRepository) {
        this.storeRepository = storeRepository;
        this.userRepository = userRepository;
        this.reservationRepository = reservationRepository;
    }


    public String userNickname(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

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

        return nickname;
    }


    public ReservationEntity postReservation(ReservationPostRequest request, Long storeId) throws  ReservationDatePassedException {
        String nickname = userNickname(); // 테스트용 nickname, 필요에 따라 별도의 사용자 정보를 얻어오도록 변경

        StoreEntity store = storeRepository.findById(Math.toIntExact(storeId))
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 매장을 찾을 수 없습니다."));

        LocalDateTime currentTime = LocalDateTime.now();

        LocalDateTime reservationStartDateTime = request.getReservationStartDateTime();
        LocalDateTime reservationEndDateTime = request.getReservationEndDateTime();

        // 시간 비교
        Duration duration = Duration.between(currentTime, reservationStartDateTime);
        if (duration.isNegative()) {
            throw new ReservationDatePassedException("예약 시간이 현재 시간보다 이전입니다. 다시 확인해주세요");
        }

        // 최종 금액

        ReservationEntity reservationEntity = ReservationEntity.builder()
                .store(store)
                .nickname(nickname)
                .reservationRole(ReservationRole.HOLD)
                .reservationStartDateTime(reservationStartDateTime)
                .reservationEndDateTime(reservationEndDateTime)
                .moneySuccessRole(MoneySuccessRole.NO_PAY)
                .registerDt(currentTime)
                .reservationAuthor(store.getAuthor())
                .build();

        reservationRepository.save(reservationEntity);

        // ReservationEntity 객체를 저장하거나 다른 비즈니스 로직을 수행할 수 있습니다.
        // ...

        return reservationEntity;
    }


    public List<ReservationEntity> findByNickname() {
        String nickname = userNickname();
        return reservationRepository.findByNickname(nickname);
    }

    public List<ReservationEntity> findByReservationAuthor() throws NoSameAutherException {
        String reservationAuthor = userNickname();
        List<ReservationEntity> findReserviationAuthor = reservationRepository.findByReservationAuthor(reservationAuthor);

        return findReserviationAuthor;
    }


    /**
     * 로직 설명 : 1분마다 업데이트를 한다 -> 예약 한지 10분이 지나면 처리 해줌 ->
     * 처리하는 것 : reservationRole 에 HOLD -> TIME_OVER 로 처리해줌
     */
    @Scheduled(fixedRate = 60000)
    @Transactional
    public void updateReservationStatus() {
        LocalDateTime currentTime = LocalDateTime.now();
        reservationRepository.updateReservationRoleToTimeOverCancel(currentTime);
    }


    public ReservationEntity updateRoleStatusByStoreOwner(Long reservationId) throws NoSameAutherException {
        ReservationEntity reservation = reservationRepository.findByReservationId(reservationId);

        String userNickname= userNickname();

        String reservationIdNickname = reservation.getReservationAuthor();

        if(!reservationIdNickname.equals(userNickname)){
            throw new NoSameAutherException("가게를 등록한 업주가 아닙니다. 다시 한번 확인하여 주세요");
        }

        reservation.setReservationRole(ReservationRole.OK);

        reservationRepository.save(reservation);

        return reservation;
    }

    /**
     * 유저가 예약취소
     * @param reservationId
     * @return
     * @throws NoSameAutherException
     */
    public ReservationEntity updateCancelStatusByUser(Long reservationId) throws NoSameAutherException {
        ReservationEntity reservation = reservationRepository.findByReservationId(reservationId);

        String userNickname = userNickname();

        String reservationIdNickname = reservation.getNickname();

        if(!reservationIdNickname.equals(userNickname)){
            throw new NoSameAutherException("가게를 등록한 고객님이 아닙니다." + userNickname +"님으로 예약하지 않으셨습니다." +
                    reservationIdNickname+ "고객님이 예약하였습니다 다시 한번 확인해주세요");
        }

        reservation.setReservationRole(ReservationRole.USER_CANCEL);

        reservationRepository.save(reservation);

        return reservation;
    }

    /**
     * 유저가 와서 예약 체크 하기
     * @param reservationId
     * @return
     * @throws NoSameAutherException
     */
    public ReservationEntity updateUserVisitKioskOk(Long reservationId) throws NoSameAutherException, RateTimeException {
        ReservationEntity reservation = reservationRepository.findByReservationId(reservationId);

        String userNickname = userNickname();

        String reservationIdNickname = reservation.getNickname();

        if(!reservationIdNickname.equals(userNickname)){
            throw new NoSameAutherException("가게를 등록한 업주가 아닙니다. 다시 한번 확인하여 주세요");
        }

        LocalDateTime startDateTime =  reservation.getReservationStartDateTime();
        LocalDateTime currentTime = LocalDateTime.now();


        // 예약 취소 가능한 마지막 시간을 현재 시간에서 10분을 뺀 시간으로 설정
        LocalDateTime cancelableDateTime = startDateTime.minusMinutes(10);

        // 예약 1시간 전에 와야지 처리해 줄 수 있음
        LocalDateTime cancel1HourVisitDateTime = startDateTime.minusMinutes(60);


        if (currentTime.isBefore(cancel1HourVisitDateTime)) {
            throw new RateTimeException("예약 시작 시간 1시간 전에 도착하여야 합니다. 가게 업주한테 문의해 주세요");
        }

        if (currentTime.isBefore(cancelableDateTime)) {
            throw new RateTimeException("예약 시작 시간보다 10분 전에 도착하여야 합니다. 가게 업주한테 문의해 주세요");
        }



        reservation.setReservationRole(ReservationRole.USER_VISIT_CONFIRMATION);

        reservationRepository.save(reservation);

        return reservation;



    }

    /**
     * 가게 점주가 예약을 받을 상황이 안되어 예약 거절
     * resevationRole -> PARTNER_USER_CANCEL 변경
     * @param reservationId
     * @return
     * @throws NoSameAutherException
     */
    public ReservationEntity updateRoleStatusByStoreOwnerCancel(Long reservationId) throws NoSameAutherException {
        ReservationEntity reservation = reservationRepository.findByReservationId(reservationId);

        String userNickname = userNickname();

        String reservationIdNickname = reservation.getReservationAuthor();

        if(!reservationIdNickname.equals(userNickname)){
            throw new NoSameAutherException("가게를 등록한 업주가 아닙니다. 다시 한번 확인하여 주세요");
        }


        reservation.setReservationRole(ReservationRole.PARTNER_USER_CANCEL);

        reservationRepository.save(reservation);

        return reservation;
    }

    /**
     * 가게 점주가 예약한 손님을 응대하고 끝냄
     * resevationRole -> PARTNER_USER_CANCEL 변경 ㅇㅇ
     * @param reservationId
     * @return
     * @throws NoSameAutherException
     */
    public ReservationEntity updateRoleStatusByStoreFinal(Long reservationId) throws NoSameAutherException {
        ReservationEntity reservation = reservationRepository.findByReservationId(reservationId);

        String userNickname = userNickname();

        String reservationIdNickname = reservation.getReservationAuthor();

        if(!reservationIdNickname.equals(userNickname)){
            throw new NoSameAutherException("가게를 등록한 업주가 아닙니다. 다시 한번 확인하여 주세요");
        }


        reservation.setMoneySuccessRole(MoneySuccessRole.OK_PAY_RESERVATION_FIN);

        reservationRepository.save(reservation);

        return reservation;
    }
}
