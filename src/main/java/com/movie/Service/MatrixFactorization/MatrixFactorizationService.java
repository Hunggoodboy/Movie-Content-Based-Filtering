package com.movie.Service.MatrixFactorization;

import com.movie.Entity.MovieVector;
import com.movie.Entity.UserMovieInteraction;
import com.movie.Entity.UserVector;
import com.movie.Repository.MovieVectorRepository;
import com.movie.Repository.UserMovieInteractionRepository;
import com.movie.Repository.UserVectorRepository;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;


@Service
@AllArgsConstructor

public class MatrixFactorizationService {
    private final UserVectorRepository userVectorRepository;
    private final MovieVectorRepository movieVectorRepository;
    private final UserMovieInteractionRepository userMovieInteractionRepository;
    private final UserMovieInteractionRepository UserMovieInteractionRepository;

    private final float learningRate = 0.01f;
    private final float regularizationParam = 0.02f;

    @Scheduled(cron = "0 0 2 * * ?") // Chạy vào lúc 2 giờ sáng hàng ngày
    public void extract(){
        List<UserMovieInteraction> userMovieInteraction = userMovieInteractionRepository.findAll();
        if(userMovieInteraction.isEmpty()){
            return;
        }
        Map<UUID, UserVector> userVectorMap = new HashMap<>();
        Map<UUID, MovieVector> movieVectorMap = new HashMap<>();
        List<UserVector> userVectorList = userVectorRepository.findAll();
        List<MovieVector> movieVectorList = movieVectorRepository.findAll();
        userVectorList.forEach(userVector -> userVectorMap.put(userVector.getUser().getId(), userVector));
        movieVectorList.forEach(movieVector -> movieVectorMap.put(movieVector.getMovie().getId(), movieVector));
        int epochs = 50;
        for(int i = 0; i < epochs; i++){
            // Học 50 lần
            for(UserMovieInteraction interaction : userMovieInteraction){
                UUID userId = interaction.getUser().getId();
                UUID movieId = interaction.getMovie().getId();
                float score = interaction.getScore();
                UserVector userVector = userVectorMap.get(userId);
                MovieVector movieVector = movieVectorMap.get(movieId);
                if(userVector == null || movieVector == null){
                    continue;
                }
                float machineGuess = divisionVector(movieVector.getVector(), userVector.getVector());
                tranModel(movieVector, userVector, score, machineGuess);
            }
        }
        userVectorRepository.saveAll(userVectorList);
        movieVectorRepository.saveAll(movieVectorList);
    }

    private void tranModel(MovieVector  movieVector, UserVector  userVector, float score, float machineGuess){
        int size = Math.min(movieVector.getVector().length, userVector.getVector().length);
        float[] oldUserVector = userVector.getVector();
        float[] oldMovieVector = movieVector.getVector();
        float[] newUserVector = new float[size];
        float[] newMovieVector = new float[size];
        float error = score - machineGuess;
        for(int i = 0; i < size; i++){
            float slopeForUser = -(error * oldMovieVector[i]) + (regularizationParam  * oldUserVector[i]);
            float slopeForMovie = -(error * oldUserVector[i]) + (regularizationParam  * oldMovieVector[i]);
            newMovieVector[i] = oldMovieVector[i] - (learningRate * slopeForMovie);
            newUserVector[i] = oldUserVector[i] - (learningRate  * slopeForUser);
        }
        movieVector.setVector(newMovieVector);
        userVector.setVector(newUserVector);
    }



    private float divisionVector(float[] vector1, float[] vector2){
        float sum = 0;
        for(int i = 0; i < Math.min(vector1.length, vector2.length); i++){
            sum += vector1[i] * vector2[i];
        }
        return sum;
    }
}
