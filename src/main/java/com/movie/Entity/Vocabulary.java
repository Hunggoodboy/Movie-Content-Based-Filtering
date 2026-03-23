package com.movie.Entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "vocabulary")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Vocabulary {

    @Id
    @Column(columnDefinition = "TEXT")
    private String term;

    // Index cố định trong vector — không bao giờ thay đổi sau khi gán
    @Column(nullable = false, unique = true)
    private Integer idx;

    // Số phim chứa từ này — tăng dần khi thêm phim mới
    @Column(nullable = false)
    private Integer df;

}