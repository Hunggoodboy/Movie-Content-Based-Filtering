package com.movie.Controller;

import com.movie.DTO.Request.MovieRequest;
import com.movie.Entity.Movie;
import com.movie.Service.CompareVectorService;
import com.movie.Service.MovieService;
import com.nimbusds.jose.JOSEException;
import lombok.AllArgsConstructor;
import movie.DTO.Response.MovieResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.List;

@RestController
@AllArgsConstructor
public class MovieController {
    private final MovieService movieService;
    private final CompareVectorService  compareVectorService;

    @PostMapping("/api/movie/post")
    public ResponseEntity<?> postMovie(@RequestBody List<MovieRequest> request) {
        for(MovieRequest movieRequest : request) {
            movieService.saveNewMovie(movieRequest);
        }
        return ResponseEntity.ok().build();
    }
    @GetMapping("/my-recommend-movie")
    public ResponseEntity<List<MovieResponse> > getMovieRecommend(@RequestHeader("Authorization") String authHeader) throws ParseException, JOSEException {
        return ResponseEntity.ok(compareVectorService.findMovieRecommendForUser(authHeader));
    }
}
