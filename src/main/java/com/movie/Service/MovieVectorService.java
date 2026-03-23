package com.movie.Service;

import com.movie.Entity.Movie;
import com.movie.Entity.MovieVector;
import com.movie.Repository.MovieVectorRepository;
import com.movie.Service.vectorizer.VectorBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MovieVectorService {

    private final MovieVectorRepository movieVectorRepository;
    private final VectorBuilder vectorBuilder;

    // Tạo và lưu vector cho phim mới
    public void createVector(Movie movie) {
        float[] vector = vectorBuilder.build(movie);
        System.out.println("Độ dài của vector: " + vector.length);
        System.out.println("Vector có dạng: " + Arrays.toString(vector));
        MovieVector movieVector = MovieVector.builder()
                .movie(movie)
                .vector(vector)
                .build();
        movieVectorRepository.save(movieVector);
    }

    // Lấy vector của 1 phim
    public float[] getVector(UUID movieId) {
        MovieVector movieVector = movieVectorRepository
                .findById(movieId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy vector cho phim: " + movieId));
        return movieVector.getVector();
    }

    // Cập nhật vector khi phim thay đổi thông tin
    public void updateVector(Movie movie) {
        MovieVector movieVector = movieVectorRepository
                .findById(movie.getId())
                .orElse(MovieVector.builder().movie(movie).build());

        movieVector.setVector(vectorBuilder.build(movie));
        movieVectorRepository.save(movieVector);
    }
}