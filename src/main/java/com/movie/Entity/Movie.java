package com.movie.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Time;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

@Entity
@Table(name = "movies")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Movie {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false)
    private UUID id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String title;

    // -------------------------------------------------------
    // Phần 1: TF-IDF đọc — người đăng nhập tự do
    // -------------------------------------------------------

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(columnDefinition = "TEXT")
    private String director;

    // "Diễn viên 1, Diễn viên 2, Diễn viên 3"
    @Column(columnDefinition = "TEXT", name = "cast_member")
    private String cast;

    // "vũ trụ, siêu anh hùng, tương lai"
    @Column(columnDefinition = "TEXT")
    private String keywords;

    // -------------------------------------------------------
    // Phần 2: One-hot encoder đọc — người đăng chọn từ menu
    // -------------------------------------------------------

    // "Mỹ", "Hàn Quốc", "Việt Nam", "Nhật Bản"
    @Column(columnDefinition = "TEXT")
    private String country;

    // "vi", "en", "ko", "ja"
    @Column(columnDefinition = "TEXT")
    private String language;

    // "P", "C13", "C16", "C18"
    @Column(name = "age_rating", columnDefinition = "TEXT")
    private String ageRating;

    // -------------------------------------------------------
    // Thông tin phụ
    // -------------------------------------------------------

    @Column(name = "release_year")
    private Integer releaseYear;

    @Column(name = "duration_mins")
    private Integer durationMins;

    @Column(name = "poster_url", columnDefinition = "TEXT")
    private String posterUrl;

    @Column(name = "external_url", columnDefinition = "TEXT")
    private String externalUrl;

    private LocalTime duration;


    // -------------------------------------------------------
    // Thống kê
    // -------------------------------------------------------

    @Column(name = "views")
    @Builder.Default
    private Long views = 0L;

    @Column(name = "avg_rating")
    @Builder.Default
    private Float avgRating = 0f;

    @Column(name = "total_ratings")
    @Builder.Default
    private Integer totalRatings = 0;
    // -------------------------------------------------------
    // termSet dùng để lưu xem phim này chứa từ gì
    // -------------------------------------------------------

    @Column(name = "term_set", columnDefinition = "TEXT")
    private String termSet;

    // -------------------------------------------------------
    // Timestamp
    // -------------------------------------------------------

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // -------------------------------------------------------
    // Quan hệ
    // -------------------------------------------------------

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "movie_genres",
            joinColumns = @JoinColumn(name = "movie_id"),
            inverseJoinColumns = @JoinColumn(name = "genre_id")
    )
    @Builder.Default
    private Set<Genre> genres = new HashSet<>();

    @OneToOne(mappedBy = "movie", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private MovieVector vector;

    // -------------------------------------------------------
    // Lifecycle
    // -------------------------------------------------------

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // -------------------------------------------------------
    // Helper cho TF-IDF
    // Chỉ ghép phần tự do — KHÔNG có country/language/ageRating
    // vì chúng đã được One-hot encoder xử lý riêng
    // -------------------------------------------------------
    public String toFeatureText() {
        StringBuilder sb = new StringBuilder();
        if (description != null) sb.append(description).append(" ");
        if (director != null)    sb.append(director).append(" ");
        if (cast != null)        sb.append(cast.replace(",", " ")).append(" ");
        if (keywords != null)    sb.append(keywords.replace(",", " ")).append(" ");
        return sb.toString().toLowerCase().trim();
    }

    //termSet cho phim
    public void buildTermSet() {
        String[] tokens = this.toFeatureText().split("\\s+");
        this.termSet = String.join(",", new HashSet<>(Arrays.asList(tokens)));
    }

    //chuyển từ termSet thành Set

    public Set<String> getTermSet() {
        if (termSet == null || termSet.isBlank()) return new HashSet<>();
        return new HashSet<>(Arrays.asList(termSet.split(",")));
    }

}