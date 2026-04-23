package com.movie.Service.MatrixFactorization;


import com.movie.DTO.Request.BehaviorRequest;
import com.movie.DTO.Response.ApiResponse;
import com.movie.DTO.Response.UserResponse;
import com.movie.Entity.Movie;
import com.movie.Entity.MovieVector;
import com.movie.Entity.UserMovieInteraction;
import com.movie.Repository.*;
import com.movie.Service.Authentication.JwtService;
import com.nimbusds.jose.JOSEException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.time.LocalTime;
import java.util.UUID;

@Service
@AllArgsConstructor
public class UserActivity {
    private final JwtService jwtService;
    private final MovieVectorRepository movieVectorRepository;
    private final MovieRepository movieRepository;
    private final UserMovieInteractionRepository userMovieInteractionRepository;
    private final UserRepository userRepository;
    public ApiResponse EvaluateUserFavourite(BehaviorRequest request, String authHeader) throws ParseException, JOSEException {
        UUID movieId = UUID.fromString(request.getMovieId());
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new RuntimeException("Movie not found"));
        System.out.println("=== KIỂM TRA DỮ LIỆU FRONTEND GỬI LÊN ===");

        System.out.println("1. Phim đang xem: " + request.getMovieId());
        System.out.println("2. Thời gian đã xem: " + request.getDurationWatch());
        System.out.println("3. Điểm đánh giá: " + request.getRating());
        System.out.println("4. Trạng thái Like: " + request.getLiked());

        System.out.println("=========================================");
        float rateByDuration = 0f;
        LocalTime watchTime = request.getDurationWatch();
        LocalTime totalTime = movie.getDuration();

        // Quy đổi ra tổng số giây
        int watchedSeconds = watchTime.toSecondOfDay();
        int totalSeconds = totalTime.toSecondOfDay();

        if (totalSeconds > 0) {
            rateByDuration = (float) watchedSeconds / totalSeconds;
            rateByDuration = Math.min(rateByDuration, 1.0f);
        }
        float rateByRating = 0.3f;
        if (request.getRating() != null) {
            rateByRating = request.getRating() / 5.0f;
        }
        float rateByLike = (request.getLiked() != null) ? request.getLiked().floatValue() : 0.0f;
        MovieVector movieVectore = movieVectorRepository.findByMovieId(movieId).orElseThrow(() -> new RuntimeException("Movie vector not found"));
        float score = (float) (0.5 * rateByDuration + 0.3 * rateByRating + 0.2 * rateByLike);
        UserResponse userResponse = jwtService.findUser(authHeader);
        UserMovieInteraction userMovieInteraction = UserMovieInteraction.builder()
                .user(userRepository.findById(UUID.fromString(userResponse.getUserId())).orElseThrow(() -> new RuntimeException("User not found")))
                .movie(movie)
                .score(score)
                .build();
        userMovieInteractionRepository.save(userMovieInteraction);
        return ApiResponse.builder()
                .success(true)
                .message("Đánh giá của bạn đã được lưu thành công!")
                .build();
    }
}
