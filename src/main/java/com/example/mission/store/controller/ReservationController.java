package com.example.mission.store.controller;


import com.example.mission.store.entity.ReservationEntity;
import com.example.mission.store.exception.NoSameAutherException;
import com.example.mission.store.exception.RateTimeException;
import com.example.mission.store.exception.ReservationDatePassedException;
import com.example.mission.store.exception.ResourceNotFoundException;
import com.example.mission.store.request.ReservationPostRequest;
import com.example.mission.store.service.ReservationService;
import com.example.mission.store.service.StoreService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/store")
public class ReservationController {

    private final StoreService storeService;
    private final ReservationService reservationService;

    public ReservationController(StoreService storeService, ReservationService reserVationService) {
        this.storeService = storeService;
        this.reservationService = reserVationService;
    }

    /**
     * 예약 등록
     * @param reservationPostRequest
     * @param storeId
     * @return
     */

    @PostMapping("/reservation/{storeId}")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<Object> postStoreReservation(
            @RequestBody ReservationPostRequest reservationPostRequest,
            @PathVariable Long storeId) {
        try {
            ReservationEntity reservationEntities = reservationService.postReservation(reservationPostRequest, storeId);

            Map<String, Object> response = new HashMap<>();
            response.put("등록 성공하였습니다!", "Success");
            response.put("reservations", reservationEntities);
            return ResponseEntity.ok(response);
        } catch (ReservationDatePassedException e) {
            Map<String, String> response = new HashMap<>();
            response.put("errorMessage", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    /**
     * 유저가 예약을 조회할 때
     *
     * @return
     * @throws ResourceNotFoundException
     */
    @GetMapping("/reservation/user/reservation/status")
    public ResponseEntity<Object> getReservationByNickname() throws ResourceNotFoundException {

        // ReservationService에서 유저의 닉네임으로 예약 정보 조회
        List<ReservationEntity> reservationEntities = reservationService.findByNickname();

        if (!reservationEntities.isEmpty()) {
            Map<String, Object> response = new HashMap<>();
            response.put("조회 성공하였습니다!", "Success");
            response.put("reservations", reservationEntities);
            return ResponseEntity.ok(response);
        } else {
            throw new ResourceNotFoundException("예약 정보를 찾을 수 없습니다.");
        }
    }

    /**
     * 가게 점주가 예약을 조회할 때
     *
     * @return
     * @throws ResourceNotFoundException
     */
    @GetMapping("/reservation/partner/user/reservation/status")
    public ResponseEntity<Object> getReservationByReservationAuthor() throws ResourceNotFoundException, NoSameAutherException {

        // ReservationService에서 유저의 닉네임으로 예약 정보 조회
        List<ReservationEntity> reservationEntities = reservationService.findByReservationAuthor();

        if (!reservationEntities.isEmpty()) {
            Map<String, Object> response = new HashMap<>();
            response.put("조회 성공하였습니다!", "Success");
            response.put("reservations", reservationEntities);
            return ResponseEntity.ok(response);
        } else {
            throw new ResourceNotFoundException("예약 정보를 찾을 수 없습니다.");
        }
    }

    /**
     * 가게 점주가 1개의 예약을 받아준다.
     * @param reservationId
     * @return
     */
    @PutMapping("/reservation/partner_user/check/status/ok/{reservationId}")
    @PreAuthorize("hasAuthority('PARTNER_USER')")
    public ResponseEntity<Object> updateReservationRoleOk(@PathVariable Long reservationId) throws NoSameAutherException {
        try {
            ReservationEntity reservationEntity = reservationService.updateRoleStatusByStoreOwner(reservationId);
            Map<String, Object> response = new HashMap<>();
            response.put("예약 상태 수정 성공하였습니다!", "Success");
            response.put("reservation", reservationEntity);
            return ResponseEntity.ok(response);
        }catch(NoSameAutherException e){
            Map<String, String> response = new HashMap<>();
            response.put("errorMessage", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    /**
     * 가게 점주가 1개의 예약을 가게 사정이 있어 취소한다.
     * @param reservationId
     * @return
     */
    @PutMapping("/reservation/partner_user/check/status/cancel/{reservationId}")
    @PreAuthorize("hasAuthority('PARTNER_USER')")
    public ResponseEntity<Object> updateReservationRolePartnerUserCancel(@PathVariable Long reservationId) throws NoSameAutherException {
        try {
            ReservationEntity reservationEntity = reservationService.updateRoleStatusByStoreOwnerCancel(reservationId);
            Map<String, Object> response = new HashMap<>();
            response.put("예약 상태 수정 성공하였습니다!", "Success");
            response.put("reservation", reservationEntity);
            return ResponseEntity.ok(response);
        }catch(NoSameAutherException e){
            Map<String, String> response = new HashMap<>();
            response.put("errorMessage", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    /**
     * 유저가 단순 변심으로 인해 예약을 취소함
     * @param reservationId
     * @return
     */
    @PutMapping("/reservation/user/check/status/cancel/{reservationId}")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<Object> updateReservationCancel(@PathVariable Long reservationId) throws NoSameAutherException {
        try {
            ReservationEntity reservationEntity = reservationService.updateCancelStatusByUser(reservationId);
            Map<String, Object> response = new HashMap<>();
            response.put("예약 취소에 성공하였습니다!", "Success");
            response.put("reservation", reservationEntity);
            return ResponseEntity.ok(response);
        } catch(NoSameAutherException e){
            Map<String, String> response = new HashMap<>();
            response.put("errorMessage", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    /**
     * 유저가 가게 10분 전에 와서 예약을 키오스크 로 확인함
     * @param reservationId
     * @return
     */
    @PutMapping("/reservation/user/check/visit/status/ok/{reservationId}")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<Object> updateReservationRole(@PathVariable Long reservationId) {
        try {
            ReservationEntity reservationEntity = reservationService.updateUserVisitKioskOk(reservationId);
            String nickname = reservationEntity.getNickname();
            Map<String, Object> response = new HashMap<>();
            response.put(nickname + "님 환영합니다!!", "Success");
            response.put("reservation", reservationEntity);
            return ResponseEntity.ok(response);
        } catch (NoSameAutherException e) {
            Map<String, String> response = new HashMap<>();
            response.put("errorMessage", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (RateTimeException e) {
            Map<String, String> response = new HashMap<>();
            response.put("errorMessage", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    /**
     * 점주가 예약 처리를 하고 손님 응대를 끝냈습니다.
     * @param reservationId
     * @return
     * @throws NoSameAutherException
     */
    @PutMapping("/reservation/partner_user/check/status/final/ok/{reservationId}")
    @PreAuthorize("hasAuthority('PARTNER_USER')")
    public ResponseEntity<Object> updateMoneySuccessRolePartnerUserOk(@PathVariable Long reservationId) throws NoSameAutherException {
        try {
            ReservationEntity reservationEntity = reservationService.updateRoleStatusByStoreFinal(reservationId);
            Map<String, Object> response = new HashMap<>();
            response.put("예약된 손님이 정상적으로 서비스를 이용하였습니다.", "Success");
            response.put("reservation", reservationEntity);
            return ResponseEntity.ok(response);
        }catch(NoSameAutherException e){
            Map<String, String> response = new HashMap<>();
            response.put("errorMessage", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }
}
