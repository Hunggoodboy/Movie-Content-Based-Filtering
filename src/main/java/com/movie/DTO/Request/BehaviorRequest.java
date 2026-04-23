package com.movie.DTO.Request;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Time;
import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BehaviorRequest {
    private String movieId;
    private LocalTime durationWatch;
    private Float rating;
    private Integer liked;
}
