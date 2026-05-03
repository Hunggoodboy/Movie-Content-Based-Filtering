package com.movie.DTO.Request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FavouriteSurveyRequest {
    List<String> genres;
    List<String> countries;
    List<String> languages;
    String age;
    String description;
}

