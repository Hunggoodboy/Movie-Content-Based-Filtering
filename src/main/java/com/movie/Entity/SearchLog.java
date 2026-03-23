package com.movie.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
    name = "search_logs",
    indexes = {
        @Index(name = "idx_search_logs_user_id",    columnList = "user_id"),
        @Index(name = "idx_search_logs_query",       columnList = "query"),
        @Index(name = "idx_search_logs_searched_at", columnList = "searched_at")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SearchLog {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false)
    private UUID id;

    // Nullable: khách vãng lai (chưa đăng nhập) cũng ghi nhận được
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = true)
    private User user;

    // Từ khóa user đã gõ vào ô tìm kiếm
    @Column(nullable = false, length = 255)
    private String query;

    // Phim user bấm vào từ kết quả tìm kiếm — NULL nếu không chọn phim nào
    // Đây là tín hiệu mạnh: tìm rồi còn chủ động click vào = quan tâm thật sự
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "clicked_movie_id", nullable = true)
    private Movie clickedMovie;

    @Column(name = "searched_at", updatable = false)
    private LocalDateTime searchedAt;

    @PrePersist
    protected void onCreate() {
        searchedAt = LocalDateTime.now();
    }
}
