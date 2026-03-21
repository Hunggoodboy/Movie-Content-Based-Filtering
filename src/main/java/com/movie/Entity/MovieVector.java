package com.movieapp.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "movie_vectors")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MovieVector {

    // Dùng chung khóa với Movie — 1 phim có đúng 1 vector
    @Id
    private UUID movieId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "movie_id")
    private Movie movie;

    // Vector TF-IDF đã tính sẵn, lưu dạng JSON: [0.12, 0.05, 0.87, ...]
    @Column(columnDefinition = "TEXT", nullable = false)
    private String vector;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    @PreUpdate
    protected void onSave() {
        updatedAt = LocalDateTime.now();
    }
}
