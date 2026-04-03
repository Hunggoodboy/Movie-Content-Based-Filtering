package com.movie.Service;

import com.movie.DTO.Request.MovieRequest;
import com.movie.Entity.Genre;
import com.movie.Entity.Movie;
import com.movie.Repository.MovieRepository;
import com.movie.Repository.GenreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class MovieService {
    
    private final MovieRepository movieRepository;
    private final GenreRepository genreRepository;
    private final MovieVectorService movieVectorService;
    public void saveNewMovie(MovieRequest request){
        Movie movie = Movie.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .director(request.getDirector())
                .cast(request.getCast())
                .keywords(request.getKeywords())
                .country(request.getCountry())
                .language(request.getLanguage())
                .ageRating(request.getAgeRating())
                .releaseYear(request.getReleaseYear())
                .durationMins(request.getDurationMins())
                .posterUrl(request.getPosterUrl())
                .externalUrl(request.getExternalUrl())
                .build();

        Set<Genre> genres = new HashSet<>(genreRepository.findByNameIn(request.getGenreNames()));
        movie.setGenres(genres);
        movie.buildTermSet();
        movieRepository.save(movie);
        movieVectorService.createVector(movie);
    }
}
