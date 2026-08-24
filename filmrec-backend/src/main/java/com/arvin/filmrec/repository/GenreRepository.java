package com.arvin.filmrec.repository;

import com.arvin.filmrec.entity.Genre;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GenreRepository extends JpaRepository<Genre, Long> {
    Optional<Genre> findByTmdbId(Integer tmdbId);
}
