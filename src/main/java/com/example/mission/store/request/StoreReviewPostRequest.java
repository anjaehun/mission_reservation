package com.example.mission.store.request;

import com.example.mission.store.entity.StoreReviewEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StoreReviewPostRequest {
    private String comment;

    private Double rating;


}
