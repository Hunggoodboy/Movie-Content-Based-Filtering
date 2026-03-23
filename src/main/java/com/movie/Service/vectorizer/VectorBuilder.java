package com.movie.Service.vectorizer;

import com.movie.Entity.Movie;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class VectorBuilder {
    private final OneHotEncoder oneHotEncoder;
    private final TF_IDF_Vectorizer tfIdfVectorizer;

    public float[] build(Movie movie) {
        float[] tf_idf_vector = tfIdfVectorizer.TF_IDF_Algorithm(movie);
        float[] one_hot_vector = oneHotEncoder.oneHotEncoder(movie);

        System.out.println("TF-IDF length: " + tf_idf_vector.length);
        System.out.println("OneHot length: " + one_hot_vector.length);
        System.out.println("vocabularyMap size: " + tfIdfVectorizer.getVocabularySize());

        float[] result = new float[tf_idf_vector.length + one_hot_vector.length];
        System.arraycopy(tf_idf_vector, 0, result, 0, tf_idf_vector.length);
        System.arraycopy(one_hot_vector, 0, result, tf_idf_vector.length, one_hot_vector.length);

        System.out.println("Final vector length: " + result.length);
        return result;
    }
}
