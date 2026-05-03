package com.movie.Repository;

import com.movie.Entity.FavouriteMovie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface FavouriteMovieRepository extends JpaRepository<FavouriteMovie, UUID> {

    // Lấy toàn bộ phim yêu thích của 1 user (dùng cho Recall@K)
    List<FavouriteMovie> findByUserId(UUID userId);

    // Kiểm tra user đã like phim này chưa (tránh duplicate)
    boolean existsByUserIdAndMovieId(UUID userId, UUID movieId);

    // Xoá 1 bản ghi like (unlike)
    void deleteByUserIdAndMovieId(UUID userId, UUID movieId);

    // Đếm số người đã like 1 phim (thống kê phổ biến)
    long countByMovieId(UUID movieId);

    // Tìm record cụ thể (để update rating/note)
    Optional<FavouriteMovie> findByUserIdAndMovieId(UUID userId, UUID movieId);
}