package com.movie.Controller;

import com.movie.DTO.Request.MovieRequest;
import com.movie.Entity.Movie;
import com.movie.Service.MovieService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor
public class MovieController {

    private final MovieService movieService;
    @PostMapping("/api/movie/post")
    public ResponseEntity<?> postMovie(@RequestBody List<MovieRequest> request) {
        for(MovieRequest movieRequest : request) {
            movieService.saveNewMovie(movieRequest);
        }
        return ResponseEntity.ok().build();
    }
}
