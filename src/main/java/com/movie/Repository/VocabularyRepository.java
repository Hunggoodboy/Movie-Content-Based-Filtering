package com.movie.Repository;

import com.movie.Entity.Vocabulary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface VocabularyRepository extends JpaRepository<Vocabulary,Long> {
    @Query("select max(v.idx) from Vocabulary v")
    public Long findMaxIdx();
    Vocabulary findByTerm(String term);
}
