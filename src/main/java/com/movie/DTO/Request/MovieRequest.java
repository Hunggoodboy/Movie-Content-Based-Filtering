package com.movie.DTO.Request;

import lombok.*;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MovieRequest {
    private String title;
    private String description;
    private String director;
    private String cast;
    private String keywords;
    private String country;
    private String language;
    private String ageRating;
    private Integer releaseYear;
    private Integer durationMins;
    private String posterUrl;
    private String externalUrl;
    private Set<String> genreNames;
}