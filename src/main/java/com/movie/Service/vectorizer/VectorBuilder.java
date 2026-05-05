package com.movie.Service.vectorizer;

import com.movie.DTO.Request.FavouriteSurveyRequest;
import com.movie.DTO.Response.ApiResponse;
import com.movie.Entity.Movie;
import com.movie.Entity.MovieVector;
import com.movie.Repository.MovieRepository;
import com.movie.Repository.MovieVectorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VectorBuilder {
    private final OneHotEncoder oneHotEncoder;
    private final TF_IDF_Vectorizer tfIdfVectorizer;
    private final MovieRepository movieRepository;
    private final MovieVectorRepository movieVectorRepository;

    @Transactional
    @Scheduled(cron = "0 0 2 * * ?") // Chạy vào lúc 2 giờ sáng hàng ngày
    public ApiResponse reBuildTFIDFVector() {
        List<Movie> movies = movieRepository.findAll();
        movies.stream().forEach(movie -> {
            try {
                MovieVector movieVector = movieVectorRepository.findByMovieId(movie.getId()).orElse(null);
                float[] vector = build(movie);
                if(movieVector != null) {
                    movieVector.setVector(vector);
                    movieVector.setUpdatedAt(LocalDateTime.now());
                    movieVectorRepository.save(movieVector);
                }
                else{
                    MovieVector newVector = MovieVector.builder()
                                                       .movie(movie)
                                                       .vector(vector)
                                                       .updatedAt(LocalDateTime.now())
                                                       .build();
                    movieVectorRepository.save(newVector);
                }
            }
            catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        });
        return ApiResponse.builder().message("Rebuild TF-IDF vector thành công").success(true).build();
    }

    public float[] build(Movie movie) {
        float[] tf_idf_vector = tfIdfVectorizer.TF_IDF_Algorithm(movie.getTitle() + movie.getDescription());
        Set<String> genres =  movie.getGenres().stream()
                .map(genre -> genre.getName())
                .collect(Collectors.toSet());
        Set<String> countries = (movie.getCountry() != null) ? Set.of(movie.getCountry()) : Set.of();
        Set<String> languages = (movie.getLanguage() != null) ? Set.of(movie.getLanguage()) : Set.of();

        float[] one_hot_vector = oneHotEncoder.oneHotEncoder(
                genres,
                countries,
                languages,
                movie.getAgeRating()
        );

        System.out.println("TF-IDF length: " + tf_idf_vector.length);
        System.out.println("OneHot length: " + one_hot_vector.length);
        System.out.println("vocabularyMap size: " + tfIdfVectorizer.getVocabularySize());

        float[] result = new float[tf_idf_vector.length + one_hot_vector.length];
        System.arraycopy(tf_idf_vector, 0, result, 0, tf_idf_vector.length);
        System.arraycopy(one_hot_vector, 0, result, tf_idf_vector.length, one_hot_vector.length);

        System.out.println("Final vector length: " + result.length);
        return tfIdfVectorizer.vectorNormalize(result);
    }

    public float[] buildFavouriteUser(FavouriteSurveyRequest request) {
        float[] tf_idf_vector = tfIdfVectorizer.TF_IDF_Algorithm(request.getDescription());

        Set<String> genres = request.getGenres().stream().collect(Collectors.toSet());
        float[] one_hot_vector = oneHotEncoder.oneHotEncoder(
                genres,
                request.getCountries().stream().collect(Collectors.toSet()),
                request.getLanguages().stream().collect(Collectors.toSet()), // Giả sử là Set
                request.getAge() // Tham số thứ 4
        );
        float[] result = new float[tf_idf_vector.length + one_hot_vector.length];
        System.arraycopy(tf_idf_vector, 0, result, 0, tf_idf_vector.length);
        System.arraycopy(one_hot_vector, 0, result, tf_idf_vector.length, one_hot_vector.length);
        return result;
    }
}
