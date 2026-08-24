package com.arvin.filmrec.controller;

import com.arvin.filmrec.dto.movie.MovieDetailResponse;
import com.arvin.filmrec.dto.movie.MovieSummaryResponse;
import com.arvin.filmrec.service.MovieService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/movies")
@RequiredArgsConstructor
@Tag(name = "Movies", description = "Browse the seeded movie catalog. Public - JWT optional (adds your rating if present).")
public class MovieController {

    private final MovieService movieService;

    @GetMapping
    public ResponseEntity<List<MovieSummaryResponse>> getAllMovies(
            @RequestParam(required = false) String search) {
        return ResponseEntity.ok(movieService.searchMovies(search));
    }

    @GetMapping("/{id}")
    public ResponseEntity<MovieDetailResponse> getMovie(@PathVariable Long id) {
        return ResponseEntity.ok(movieService.getMovieDetail(id));
    }
}
