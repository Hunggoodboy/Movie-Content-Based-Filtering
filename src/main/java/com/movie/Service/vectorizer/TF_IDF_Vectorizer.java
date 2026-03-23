package com.movie.Service.vectorizer;


import com.movie.Entity.Movie;
import com.movie.Entity.Vocabulary;
import com.movie.Repository.MovieRepository;
import com.movie.Repository.VocabularyRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TF_IDF_Vectorizer {
    private final MovieRepository movieRepository;
    private final VocabularyRepository vocabularyRepository;


    private Map<String, Integer> dfMap = new HashMap<>();
    private Map<String, Integer> vocabularyMap = new HashMap<>();

    private void buildVocabulary(){
        List<Vocabulary> vocabularies = vocabularyRepository.findAll();
        vocabularyMap = vocabularies.stream()
                .collect(Collectors.toMap(
                        Vocabulary::getTerm,
                        Vocabulary::getIdx
                ));
    }

    private void buildDF(){
        List<Movie> movies = movieRepository.findAll();
        for(Movie m : movies){
            Set<String> termSet = m.getTermSet();
            for (String term : termSet) {
                if(dfMap.containsKey(term)){
                    dfMap.put(term,dfMap.get(term)+1);
                }
                else{
                    dfMap.put(term,1);
                }
            }
        }

    }
    @PostConstruct
    private void init() {
        buildVocabulary();
        buildDF();
    }

    private void addToVocabulary(String[] texts){
        Long maxIdx = vocabularyRepository.findMaxIdx();
        int nextIdx = (maxIdx == null) ? 0 : (int)(maxIdx + 1);
        for(String term : texts){
            if(!vocabularyMap.containsKey(term)){
                int df = 1;
                if(dfMap.containsKey(term)){
                    df = dfMap.get(term) + 1;
                    dfMap.put(term, df);
                }
                else{
                    dfMap.put(term, 1);
                }
                vocabularyRepository.save(new Vocabulary(term, nextIdx , df));
                vocabularyMap.put(term, nextIdx);
                nextIdx++;
            }
        }
    }

    private float[] vectorNormalize(float[] vector){
        float vec_length = 0;
        for(int i = 0 ; i < vector.length ; i++){
            vec_length += vector[i] * vector[i];
        }
        vec_length = (float) Math.sqrt(vec_length);
        if(vec_length == 0) return vector;
        for(int i = 0 ; i < vector.length ; i++){
            vector[i] /= vec_length;
        }
        return  vector;
    }

    public float[] TF_IDF_Algorithm (Movie movie) {
        String[] text = movie.toFeatureText().split("\\s+");
        Map<String, Integer> dfCount = new HashMap<>();
        for(String term : text){
            if(dfCount.containsKey(term)){
                dfCount.put(term, dfCount.get(term) + 1);
            }
            else{
                dfCount.put(term, 1);
            }
        }
        addToVocabulary(text);
        float[] result = new float[300];
        int n = (int) movieRepository.count();
        for(String term : text){
            Integer idx = vocabularyMap.get(term);
            if(idx == null || idx >= 300) continue;
            float tf =(float)dfCount.get(term) / text.length;
            float idf =(float)Math.log( (double) n / dfMap.get(term) );
            result[idx] = tf * idf;
        }
        return vectorNormalize(result);
    }
    public int getVocabularySize() {
        return vocabularyMap.size();
    }
}
