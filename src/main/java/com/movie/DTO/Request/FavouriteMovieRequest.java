package com.movie.DTO.Request;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
public class FavouriteMovieRequest {

    // ID phim muốn like/unlike
    private UUID movieId;

    // Rating tuỳ chọn (1.0 - 5.0)
    private Float rating;

    // Ghi chú tuỳ chọn
    private String note;

    // --- Các field dùng cho VectorBuilder.buildFavouriteUser() ---
    private String description;
    private List<String> genres;
    private List<String> countries;
    private List<String> languages;
    private String age;
}