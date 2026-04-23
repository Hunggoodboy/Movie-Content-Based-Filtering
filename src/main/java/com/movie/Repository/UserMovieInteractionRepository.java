package com.movie.Repository;

import com.movie.Entity.UserMovieInteraction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface UserMovieInteractionRepository extends JpaRepository<UserMovieInteraction, UUID> {
    List<UserMovieInteraction> findByUserId(UUID userId);
}
