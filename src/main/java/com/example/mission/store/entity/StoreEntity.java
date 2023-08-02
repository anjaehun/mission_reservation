package com.example.mission.store.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "store")
public class StoreEntity {
    @Id
    @GeneratedValue(strategy =  GenerationType.AUTO)
    private Integer storeId; // 매장 pk

    private String storeName; // 매장 이름

    private String location; // 위치

    private double xCoordinate; // x좌표 추가

    private double yCoordinate; // y좌표 추가

    @Column(columnDefinition = "TEXT") // Text 타입으로 지정
    private String explanation; // 설명

    private String storeImageUrlOne;   // 이미지 url One

    private String storeImageUrlTwo;    // 이미지 url Two

    private String storeImageUrlThree;  // 이미지 url Three


    private Double star; // 가게의 평점 평균 들어갈 것

    private String author; // 가게를 등록한 사용자의 정보

    private LocalDateTime registerDt; // 가게 등록일시

    private LocalDateTime modifiedDt; // 가게 정보가 마지막으로 수정된 일시

    private boolean isOpen; // 가게 운영 여부

    @Transient // 데이터베이스에 저장하지 않을 필드로 지정
    private String errorMessage; // 예외가 발생했을 때 저장될 에러 메시지


    public void setIsOpen(boolean open) {
    }





}
