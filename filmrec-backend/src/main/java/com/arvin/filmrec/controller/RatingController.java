package com.arvin.filmrec.controller;

import com.arvin.filmrec.dto.rating.RatingRequest;
import com.arvin.filmrec.dto.rating.RatingResponse;
import com.arvin.filmrec.service.RatingService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ratings")
@RequiredArgsConstructor
@Tag(name = "Ratings", description = "Rate movies (requires auth). Your ratings are private to your account.")
public class RatingController {

    private final RatingService ratingService;

    @PostMapping
    public ResponseEntity<RatingResponse> rateMovie(@Valid @RequestBody RatingRequest request) {
        return ResponseEntity.ok(ratingService.rateMovie(request));
    }

    @GetMapping("/me")
    public ResponseEntity<List<RatingResponse>> getMyRatings() {
        return ResponseEntity.ok(ratingService.getMyRatings());
    }

    @DeleteMapping("/{movieId}")
    public ResponseEntity<Void> deleteRating(@PathVariable Long movieId) {
        ratingService.deleteRating(movieId);
        return ResponseEntity.noContent().build();
    }
}
