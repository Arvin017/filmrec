package com.arvin.filmrec.controller;

import com.arvin.filmrec.service.TmdbSeederService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Tag(name = "Admin", description = "One-off catalog seeding from TMDB. Requires auth (any logged-in user, for a portfolio project).")
public class AdminSeedController {

    private final TmdbSeederService tmdbSeederService;

    @PostMapping("/seed")
    public ResponseEntity<TmdbSeederService.SeedResult> seed(
            @RequestParam(required = false) Integer pages) {
        return ResponseEntity.ok(tmdbSeederService.seed(pages));
    }
}
