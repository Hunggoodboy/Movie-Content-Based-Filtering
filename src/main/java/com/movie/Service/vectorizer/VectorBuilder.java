package com.movie.Service.vectorizer;

import com.movie.DTO.Request.FavouriteSurveyRequest;
import com.movie.Entity.Movie;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VectorBuilder {
    private final OneHotEncoder oneHotEncoder;
    private final TF_IDF_Vectorizer tfIdfVectorizer;

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
        return result;
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
