package com.movie.Controller;

import com.movie.DTO.Request.LikeMovieRequest;
import com.movie.DTO.Request.MovieRequest;
import com.movie.DTO.Response.ApiResponse;
import com.movie.Service.CompareVectorService;
import com.movie.Service.MovieService;
import com.movie.Service.vectorizer.VectorBuilder;
import com.nimbusds.jose.JOSEException;
import lombok.AllArgsConstructor;
import com.movie.DTO.Response.MovieResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.List;
import java.util.UUID;

@RestController
@AllArgsConstructor
public class MovieController {
    private final MovieService movieService;
    private final CompareVectorService  compareVectorService;
    private final VectorBuilder vectorBuilder;

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

    @GetMapping("/api/movie-detail")
    public ResponseEntity<MovieResponse> getMovieDetail(@RequestParam UUID movieId) {
        return ResponseEntity.ok(movieService.getMovieResponse(movieId));
    }

    @PostMapping("/api/like-movie")
    public ResponseEntity<?> likeMovie(@RequestHeader("Authorization") String authHeader, @RequestBody LikeMovieRequest request) throws ParseException, JOSEException {
        return ResponseEntity.ok(movieService.likeMovie(request, authHeader));
    }

    @PostMapping("/api/rebuild")
    public ResponseEntity<ApiResponse> triggerRebuildVectors() {
        ApiResponse response = vectorBuilder.reBuildTFIDFVector();
        return ResponseEntity.ok(response);
    }
}



 
