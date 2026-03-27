package com.movie.Service;

import com.movie.Entity.MovieVector;
import com.movie.Entity.UserVector;
import com.movie.Repository.MovieRepository;
import com.movie.Repository.MovieVectorRepository;
import com.movie.Repository.UserRepository;
import com.movie.Repository.UserVectorRepository;
import com.nimbusds.jose.JOSEException;
import movie.DTO.Response.MovieResponse;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CompareVectorService {

    private UserVectorService userVectorService;
    private MovieVectorRepository movieVectorRepository;
    private UserRepository userRepository;
    private MovieRepository movieRepository;

    public float cosineForVector(float[] vector1, float[] vector2){
        int length = Math.min(vector1.length,vector2.length);
        float up = 0;
        for(int i = 0; i < length; i++){
            up += vector1[i]*vector2[i];
        }
        float down1 = 0;
        for(int i = 0; i < vector1.length; i++){
            down1 += vector1[i] * vector1[i];
        }
        float down2 = 0;
        for(int i = 0; i < vector2.length; i++) {
            down2 += vector2[i] * vector2[i];
        }
        return (float) (up/(Math.sqrt(down1) * Math.sqrt(down2)));
    }

    public List<MovieResponse> findMovieRecommendForUser (String authHeader) throws ParseException, JOSEException {
        List< MovieVector> movieVectors = movieVectorRepository.findAll();
        try {
            UserVector userVector = userVectorService.getUserVector(authHeader);
            return movieVectors.stream()
                    .map(movieVector -> {
                        float similarScore = cosineForVector(userVector.getVector(), movieVector.getVector());
                        return new AbstractMap.SimpleEntry<>(movieVector.getMovie(), similarScore);
                    })
                    .sorted((entry1, entry2) -> Float.compare(entry1.getValue(), entry2.getValue()))
                    .map(entry -> MovieResponse.fromEntity(entry.getKey()))
                    .toList();
        }
        catch (Exception e){
            return movieRepository.findAll().stream()
                    .map(movie -> MovieResponse.fromEntity(movie))
                    .collect(Collectors.toList());
        }
    }
}
