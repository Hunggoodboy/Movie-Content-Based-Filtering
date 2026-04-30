package com.movie.Service;

import com.movie.DTO.Request.MovieRequest;
import com.movie.DTO.Response.MovieResponse;
import com.movie.Entity.Genre;
import com.movie.Entity.Movie;
import com.movie.Repository.MovieRepository;
import com.movie.Repository.GenreRepository;
import com.movie.Service.vectorizer.MovieVectorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

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

        Set<String> requestedGenreNames = request.getGenreNames();
        if (requestedGenreNames != null && !requestedGenreNames.isEmpty()) {
            // 1. Tìm các genre đã có
            List<Genre> existingGenres = genreRepository.findByNameIn(requestedGenreNames);
            Set<Genre> finalGenres = new HashSet<>(existingGenres);

            // 2. Tìm ra những cái tên chưa có trong DB
            Set<String> existingNames = existingGenres.stream()
                                                      .map(Genre::getName) // Giả định Entity Genre có trường name
                                                      .collect(Collectors.toSet());

            for (String name : requestedGenreNames) {
                if (!existingNames.contains(name)) {
                    // Tạo mới và thêm vào tập hợp (Hibernate sẽ tự lo việc lưu vào bảng genres nhờ Cascade)
                    Genre newGenre = Genre.builder().name(name).build();
                    finalGenres.add(newGenre);
                }
            }
            movie.setGenres(finalGenres);
        }
        movie.buildTermSet();
        movieRepository.save(movie);
        movieVectorService.createVector(movie);
    }

    public MovieResponse getMovieResponse(UUID movieId){
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phim với ID: " + movieId));
        System.out.println("-------------Thông tin phim: -------------");
        try {
            ObjectMapper mapper = new ObjectMapper();
            // In ra string với format JSON căn lề, xuống dòng gọn gàng (Pretty Print)
            String jsonString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(movie);
            System.out.println(jsonString);
        } catch (Exception e) {
            System.out.println("Lỗi khi chuyển đổi đối tượng sang JSON: " + e.getMessage());
        }
        return MovieResponse.fromEntity(movie);
    }
}
