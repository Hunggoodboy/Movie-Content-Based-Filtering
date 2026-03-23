package com.movie.Entity;

import com.movie.Config.VectorType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "user_vectors")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserVector {

    // Dùng chung khóa với User — 1 user có đúng 1 vector
    @Id
    private UUID userId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;

    // Vector sở thích của user — cùng số chiều với MovieVector (384)
    // Được tính bằng weighted sum của các MovieVector mà user đã tương tác:
    //   - Bấm "Xem ngay"         → cộng MovieVector vào với trọng số nhỏ
    //   - Đánh giá 4-5 sao       → cộng MovieVector vào với trọng số lớn
    //   - Đánh giá 1-2 sao       → trừ MovieVector ra (trọng số âm)
    //   - Tìm kiếm + click phim  → cộng MovieVector vào với trọng số vừa
    @Column(columnDefinition = "vector(384)", nullable = false)
    @org.hibernate.annotations.Type(VectorType.class)
    private float[] vector;

    // Số lượng tương tác đã dùng để tính vector này
    // Càng nhiều tương tác → vector càng chính xác
    @Column(name = "interaction_count")
    @Builder.Default
    private Integer interactionCount = 0;

    // Lần cuối vector được rebuild
    // Mỗi khi user có tương tác mới → cần rebuild lại
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    @PreUpdate
    protected void onSave() {
        updatedAt = LocalDateTime.now();
    }
}