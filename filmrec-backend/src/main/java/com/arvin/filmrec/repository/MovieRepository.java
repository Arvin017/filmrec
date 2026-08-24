package com.arvin.filmrec.repository;

import com.arvin.filmrec.entity.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MovieRepository extends JpaRepository<Movie, Long> {
    Optional<Movie> findByTmdbId(Integer tmdbId);

    @Query("SELECT DISTINCT m FROM Movie m " +
           "LEFT JOIN m.genres g LEFT JOIN m.directors d LEFT JOIN m.actors a " +
           "WHERE LOWER(m.title) LIKE LOWER(CONCAT('%', :q, '%')) " +
           "OR LOWER(g.name) LIKE LOWER(CONCAT('%', :q, '%')) " +
           "OR LOWER(d.name) LIKE LOWER(CONCAT('%', :q, '%')) " +
           "OR LOWER(a.name) LIKE LOWER(CONCAT('%', :q, '%'))")
    List<Movie> search(@Param("q") String query);

    @Query("SELECT m FROM Movie m WHERE m.id NOT IN " +
           "(SELECT r.movie.id FROM Rating r WHERE r.user.id = :userId)")
    List<Movie> findUnratedByUser(@Param("userId") Long userId);
}
