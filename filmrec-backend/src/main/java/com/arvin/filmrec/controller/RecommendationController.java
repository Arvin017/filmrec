package com.arvin.filmrec.controller;

import com.arvin.filmrec.dto.recommendation.RecommendationResponse;
import com.arvin.filmrec.service.RecommendationService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recommendations")
@RequiredArgsConstructor
@Tag(name = "Recommendations", description = "Personalized, content-based recommendations with a 'why' reason. Requires auth.")
public class RecommendationController {

    private final RecommendationService recommendationService;

    @GetMapping
    public ResponseEntity<List<RecommendationResponse>> getRecommendations(
            @RequestParam(required = false) Integer limit) {
        return ResponseEntity.ok(recommendationService.getRecommendations(limit));
    }
}
