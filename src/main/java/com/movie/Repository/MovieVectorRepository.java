package com.movie.Repository;


import com.movie.Entity.MovieVector;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface MovieVectorRepository extends JpaRepository<MovieVector, UUID> {
    Optional<MovieVector> findById(UUID id);

    Optional<MovieVector> findByMovieId(UUID movieId);
}
