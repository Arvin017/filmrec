package com.arvin.filmrec.repository;

import com.arvin.filmrec.entity.Director;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DirectorRepository extends JpaRepository<Director, Long> {
    Optional<Director> findByTmdbId(Integer tmdbId);
}
