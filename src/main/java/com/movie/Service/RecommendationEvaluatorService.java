package com.movie.Service;

import com.movie.Entity.MovieVector;
import com.movie.Entity.UserVector;
import com.movie.Repository.FavouriteMovieRepository;
import com.movie.Repository.MovieVectorRepository;
import com.nimbusds.jose.JOSEException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Đánh giá hiệu quả hệ thống gợi ý phim theo Precision@K và Recall@K.
 *
 * Ground truth: danh sách phim người dùng đã đánh dấu yêu thích (FavouriteMovie).
 * Predicted:    top-K phim có cosine similarity cao nhất với UserVector.
 */
@Service
@RequiredArgsConstructor
public class RecommendationEvaluatorService {

    private final UserVectorService userVectorService;
    private final MovieVectorRepository movieVectorRepository;
    private final CompareVectorService compareVectorService;
    private final FavouriteMovieRepository favouriteMovieRepository;

    // DTO kết quả đánh giá
    public record EvaluationResult(
            int k,
            int hits,
            int likedTotal,
            float precisionAtK,
            float recallAtK,
            List<UUID> topKMovieIds,
            List<UUID> likedMovieIds
    ) {}


    public EvaluationResult evaluate(String authHeader, int k) throws ParseException, JOSEException {

        // 1. Lấy vector người dùng
        UserVector userVector = userVectorService.getUserVector(authHeader);
        UUID userId = userVector.getUser().getId();

        // 2. Ground truth: UUID các phim user đã like
        List<UUID> likedMovieIds = favouriteMovieRepository
                .findByUserId(userId)
                .stream()
                .map(fav -> fav.getMovie().getId())
                .collect(Collectors.toList());

        if (likedMovieIds.isEmpty()) {
            return new EvaluationResult(k, 0, 0, 0f, 0f, List.of(), List.of());
        }

        // 3. Top-K dự đoán: sắp xếp toàn bộ MovieVector theo cosine similarity giảm dần
        List<UUID> topKMovieIds = movieVectorRepository.findAll().stream()
                                                       .map(mv -> new AbstractMap.SimpleEntry<>(
                                                               mv.getMovie().getId(),
                                                               compareVectorService.cosineForVector(userVector.getVector(), mv.getVector())
                                                       ))
                                                       .sorted((a, b) -> Float.compare(b.getValue(), a.getValue()))
                                                       .limit(k)
                                                       .map(Map.Entry::getKey)
                                                       .collect(Collectors.toList());

        // 4. Tính hits = giao nhau giữa top-K và liked
        Set<UUID> likedSet = new HashSet<>(likedMovieIds);
        long hits = topKMovieIds.stream().filter(likedSet::contains).count();

        // 5. Precision@K = hits / K
        float precisionAtK = (float) hits / k;

        // 6. Recall@K = hits / |liked|
        float recallAtK = (float) hits / likedMovieIds.size();

        return new EvaluationResult(
                k,
                (int) hits,
                likedMovieIds.size(),
                precisionAtK,
                recallAtK,
                topKMovieIds,
                likedMovieIds
        );
    }

    /**
     * Tính trung bình Precision@K và Recall@K trên nhiều người dùng (macro-average).
     * Dùng để đánh giá tổng thể hệ thống.
     *
     * @param userIds danh sách userId cần đánh giá
     * @param k       top-K
     */
    public Map<String, Float> evaluateMacroAverage(List<UUID> userIds, int k) {
        // NOTE: method này dùng userId trực tiếp, cần override evaluate() nhận UUID
        // hoặc tạo thêm overload. Để demo, trả về map rỗng.
        // Implement thêm tùy theo kiến trúc auth của bạn.
        throw new UnsupportedOperationException("Implement thêm evaluate(UUID userId, int k)");
    }
}