package com.movieapp.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

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

    @Column(nullable = false, length = 500)
    private String title;

    // Dùng để TF-IDF vectorize: mô tả nội dung phim
    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(length = 255)
    private String director;

    // Danh sách diễn viên, lưu dạng "Nam1, Nam2, Nam3"
    @Column(columnDefinition = "TEXT")
    private String cast;

    // Từ khóa nội dung: "vũ trụ, anh hùng, tương lai"
    @Column(columnDefinition = "TEXT")
    private String keywords;

    @Column(name = "release_year")
    private Integer releaseYear;

    @Column(name = "poster_url", length = 500)
    private String posterUrl;

    // Link tới trang ngoài (YouTube, trang giới thiệu...) — không lưu video
    @Column(name = "external_url", length = 500)
    private String externalUrl;

    @Column(name = "avg_rating")
    @Builder.Default
    private Float avgRating = 0f;

    @Column(name = "total_ratings")
    @Builder.Default
    private Integer totalRatings = 0;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    // Quan hệ nhiều-nhiều với Genre qua bảng movie_genres
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "movie_genres",
        joinColumns = @JoinColumn(name = "movie_id"),
        inverseJoinColumns = @JoinColumn(name = "genre_id")
    )
    @Builder.Default
    private Set<Genre> genres = new HashSet<>();

    // Quan hệ 1-1 với vector (load lazy, chỉ khi cần recommend)
    @OneToOne(mappedBy = "movie", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private MovieVector vector;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    // Helper: ghép toàn bộ text để TF-IDF đọc
    public String toFeatureText() {
        StringBuilder sb = new StringBuilder();
        if (description != null) sb.append(description).append(" ");
        if (director != null)    sb.append(director).append(" ");
        if (cast != null)        sb.append(cast.replace(",", " ")).append(" ");
        if (keywords != null)    sb.append(keywords.replace(",", " ")).append(" ");
        genres.forEach(g -> sb.append(g.getName()).append(" "));
        return sb.toString().toLowerCase().trim();
    }
}
