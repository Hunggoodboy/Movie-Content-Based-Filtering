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

        Set<UUID> likedSet = new HashSet<>(likedMovieIds);
        long hits = topKMovieIds.stream().filter(likedSet::contains).count();
        float precisionAtK = (float) hits / k;
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

}