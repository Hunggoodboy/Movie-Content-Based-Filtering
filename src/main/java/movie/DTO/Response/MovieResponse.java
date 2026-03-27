package movie.DTO.Response;

import com.movie.Entity.Movie;
import lombok.*;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MovieResponse {

    private UUID id;
    private String title;

    // Thông tin chi tiết
    private String description;
    private String director;
    private String cast;
    private String keywords;

    // Phân loại
    private String country;
    private String language;
    private String ageRating;

    // Thể loại (Chỉ trả về danh sách tên thể loại thay vì cả Object)
    private Set<String> genres;

    // Thông tin phụ
    private Integer releaseYear;
    private Integer durationMins;
    private String posterUrl;
    private String externalUrl;

    // Thống kê
    private Long views;
    private Float avgRating;
    private Integer totalRatings;

    /**
     * Hàm tiện ích giúp chuyển đổi nhanh từ Movie Entity sang MovieResponse
     */
    public static MovieResponse fromEntity(Movie movie) {
        if (movie == null) {
            return null;
        }

        return MovieResponse.builder()
                .id(movie.getId())
                .title(movie.getTitle())
                .description(movie.getDescription())
                .director(movie.getDirector())
                .cast(movie.getCast())
                .keywords(movie.getKeywords())
                .country(movie.getCountry())
                .language(movie.getLanguage())
                .ageRating(movie.getAgeRating())
                // Chuyển Set<Genre> thành Set<String> chỉ chứa tên thể loại
                .genres(movie.getGenres() != null
                        ? movie.getGenres().stream()
                        .map(genre -> genre.getName()) // Giả sử entity Genre có hàm getName()
                        .collect(Collectors.toSet())
                        : null)
                .releaseYear(movie.getReleaseYear())
                .durationMins(movie.getDurationMins())
                .posterUrl(movie.getPosterUrl())
                .externalUrl(movie.getExternalUrl())
                .views(movie.getViews())
                .avgRating(movie.getAvgRating())
                .totalRatings(movie.getTotalRatings())
                .build();
    }
}