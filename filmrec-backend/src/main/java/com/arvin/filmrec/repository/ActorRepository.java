package com.arvin.filmrec.repository;

import com.arvin.filmrec.entity.Actor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ActorRepository extends JpaRepository<Actor, Long> {
    Optional<Actor> findByTmdbId(Integer tmdbId);
}
