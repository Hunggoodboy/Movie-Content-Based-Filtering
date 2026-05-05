package com.movie.Service;

import com.movie.DTO.Response.MovieResponse;
import com.movie.Entity.MovieVector;
import com.movie.Entity.UserVector;
import com.movie.Repository.MovieRepository;
import com.movie.Repository.MovieVectorRepository;
import com.movie.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.AbstractMap;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompareVectorService {

    private final UserVectorService userVectorService;
    private final MovieVectorRepository movieVectorRepository;
    private final MovieRepository movieRepository;

    public float cosineForVector(float[] vector1, float[] vector2){
        int length = Math.min(vector1.length, vector2.length);

        float dot = 0;
        for(int i = 0; i < length; i++){
            dot += vector1[i] * vector2[i];
        }

        float norm1 = 0;
        for(float v : vector1){
            norm1 += v * v;
        }

        float norm2 = 0;
        for(float v : vector2){
            norm2 += v * v;
        }

        if(norm1 == 0 || norm2 == 0) return 0;

        return (float) (dot / (Math.sqrt(norm1) * Math.sqrt(norm2)));
    }

    public List<MovieResponse> findMovieRecommendForUser(String authHeader)
            throws ParseException {

        List<MovieVector> movieVectors = movieVectorRepository.findAll();

        try {
            UserVector userVector = userVectorService.getUserVector(authHeader);
            System.out.println("Các phim được gợi và điểm cosine similarity với vector người dùng:");
            List<MovieResponse> movieResponses = movieVectors.stream()
                 .map(movieVector -> {
                     float score = cosineForVector(
                             userVector.getVector(),
                             movieVector.getVector()
                     );
                     return new AbstractMap.SimpleEntry<>(movieVector.getMovie(), score);
                 })
                 // Sắp xếp GIẢM DẦN (phim giống nhất lên đầu)
                 .sorted((a, b) -> Float.compare(b.getValue(), a.getValue()))
                 // Thêm peek() để in log tên phim và score
                 .peek(entry -> {
                     // Giả sử entity Movie của bạn có hàm getTitle() hoặc getName()
                     String movieName = entry.getKey().getTitle();
                     float score = entry.getValue();

                     System.out.println("Tên phim: " + movieName + " | Score: " + score);
                 })
                 .map(entry -> MovieResponse.fromEntity(entry.getKey()))
                 .toList();
            return movieResponses;
        } catch (Exception e) {
            // fallback nếu user chưa có vector
            System.err.println("Lỗi khi xử lý vector người dùng: " + e.getMessage());
            return movieRepository.findAll()
                    .stream()
                    .map(MovieResponse::fromEntity)
                    .collect(Collectors.toList());
        }
    }
}