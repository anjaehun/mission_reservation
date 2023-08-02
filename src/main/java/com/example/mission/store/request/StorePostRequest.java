package com.example.mission.store.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StorePostRequest {

    private String storeName;

    private String location;

    private String explanation;

    private String author;

    private String storeImageUrlOne;   // 이미지 url One

    private String storeImageUrlTwo;    // 이미지 url Two

    private String storeImageUrlThree;  // 이미지 url Three

    private boolean isOpen;

    private Double xCoordinate;

    private Double yCoordinate;
}
