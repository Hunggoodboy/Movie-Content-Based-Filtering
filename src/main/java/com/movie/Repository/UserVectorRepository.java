package com.movie.Repository;

import com.movie.Entity.UserVector;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserVectorRepository extends JpaRepository<UserVector, UUID> {
    Optional<UserVector> findByUserId(UUID userId);
}
