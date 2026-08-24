package com.arvin.filmrec.service;

import com.arvin.filmrec.document.RecommendationLog;
import com.arvin.filmrec.dto.recommendation.RecommendationResponse;
import com.arvin.filmrec.entity.Actor;
import com.arvin.filmrec.entity.Director;
import com.arvin.filmrec.entity.Genre;
import com.arvin.filmrec.entity.Movie;
import com.arvin.filmrec.entity.Rating;
import com.arvin.filmrec.entity.User;
import com.arvin.filmrec.repository.MovieRepository;
import com.arvin.filmrec.repository.RatingRepository;
import com.arvin.filmrec.repository.mongo.RecommendationLogRepository;
import com.arvin.filmrec.security.CurrentUserProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Core recommendation logic.
 *
 * For every movie the user hasn't rated yet, we score it by comparing it against
 * every movie the user HAS rated, looking for shared directors, genres, and actors.
 * Each shared attribute contributes (weight * normalizedUserScore) to the candidate's
 * total score, where normalizedUserScore = userRating / 5.0 - so a film you rated 5
 * stars pulls its "neighbors" into your recommendations much harder than one you rated 2 stars.
 *
 * Weights: director (3.0) > genre (2.0) > actor (1.0), reflecting that directorial style
 * tends to be the strongest signal of "this will feel like that", genre next, and shared
 * cast the weakest (a shared actor in a bit part says less than a shared genre).
 */
@Service
@RequiredArgsConstructor
public class RecommendationService {

    private static final double DIRECTOR_WEIGHT = 3.0;
    private static final double GENRE_WEIGHT = 2.0;
    private static final double ACTOR_WEIGHT = 1.0;
    private static final int DEFAULT_LIMIT = 10;

    private final MovieRepository movieRepository;
    private final RatingRepository ratingRepository;
    private final CurrentUserProvider currentUserProvider;
    private final RecommendationLogRepository recommendationLogRepository;

    @Transactional(readOnly = true)
    public List<RecommendationResponse> getRecommendations(Integer limit) {
        User user = currentUserProvider.getRequiredCurrentUser();
        int effectiveLimit = (limit == null || limit <= 0) ? DEFAULT_LIMIT : limit;

        List<Rating> myRatings = ratingRepository.findByUserId(user.getId());
        if (myRatings.isEmpty()) {
            return List.of();
        }

        List<Movie> candidates = movieRepository.findUnratedByUser(user.getId());

        List<ScoredCandidate> scored = new ArrayList<>();
        for (Movie candidate : candidates) {
            ScoredCandidate result = scoreCandidate(candidate, myRatings);
            if (result.score > 0) {
                scored.add(result);
            }
        }

        scored.sort(Comparator.comparingDouble((ScoredCandidate c) -> c.score).reversed());

        List<ScoredCandidate> top = scored.stream().limit(effectiveLimit).toList();

        logRecommendations(user.getId(), top);

        return top.stream().map(this::toResponse).toList();
    }

    private ScoredCandidate scoreCandidate(Movie candidate, List<Rating> myRatings) {
        double totalScore = 0.0;

        // Track the single strongest contribution to build a human-readable "why" reason.
        double bestContribution = 0.0;
        String bestReason = null;

        for (Rating myRating : myRatings) {
            Movie ratedMovie = myRating.getMovie();
            double normalizedUserScore = myRating.getScore() / 5.0;

            // Shared directors
            for (Director d : candidate.getDirectors()) {
                if (ratedMovie.getDirectors().contains(d)) {
                    double contribution = DIRECTOR_WEIGHT * normalizedUserScore;
                    totalScore += contribution;
                    if (contribution > bestContribution) {
                        bestContribution = contribution;
                        bestReason = String.format(
                                "Shares director %s with \"%s\", which you rated %d/5",
                                d.getName(), ratedMovie.getTitle(), myRating.getScore());
                    }
                }
            }

            // Shared genres
            for (Genre g : candidate.getGenres()) {
                if (ratedMovie.getGenres().contains(g)) {
                    double contribution = GENRE_WEIGHT * normalizedUserScore;
                    totalScore += contribution;
                    if (contribution > bestContribution) {
                        bestContribution = contribution;
                        bestReason = String.format(
                                "Shares genre %s with \"%s\", which you rated %d/5",
                                g.getName(), ratedMovie.getTitle(), myRating.getScore());
                    }
                }
            }

            // Shared actors
            for (Actor a : candidate.getActors()) {
                if (ratedMovie.getActors().contains(a)) {
                    double contribution = ACTOR_WEIGHT * normalizedUserScore;
                    totalScore += contribution;
                    if (contribution > bestContribution) {
                        bestContribution = contribution;
                        bestReason = String.format(
                                "Shares actor %s with \"%s\", which you rated %d/5",
                                a.getName(), ratedMovie.getTitle(), myRating.getScore());
                    }
                }
            }
        }

        if (bestReason == null) {
            bestReason = "Matches the general style of films you've rated highly";
        } else {
            bestReason = "Recommended because it " + lowerFirst(bestReason);
        }

        return new ScoredCandidate(candidate, totalScore, bestReason);
    }

    private String lowerFirst(String s) {
        if (s == null || s.isEmpty()) return s;
        return Character.toLowerCase(s.charAt(0)) + s.substring(1);
    }

    private void logRecommendations(Long userId, List<ScoredCandidate> top) {
        List<RecommendationLog.RecommendationEntry> entries = top.stream()
                .map(c -> RecommendationLog.RecommendationEntry.builder()
                        .movieId(c.movie.getId())
                        .movieTitle(c.movie.getTitle())
                        .score(c.score)
                        .reason(c.reason)
                        .build())
                .toList();

        RecommendationLog log = RecommendationLog.builder()
                .userId(userId)
                .recommendations(entries)
                .build();

        recommendationLogRepository.save(log);
    }

    private RecommendationResponse toResponse(ScoredCandidate c) {
        return RecommendationResponse.builder()
                .movieId(c.movie.getId())
                .title(c.movie.getTitle())
                .posterUrl(c.movie.getPosterUrl())
                .releaseYear(c.movie.getReleaseYear())
                .tmdbRating(c.movie.getTmdbRating())
                .score(Math.round(c.score * 100.0) / 100.0)
                .reason(c.reason)
                .build();
    }

    private static class ScoredCandidate {
        final Movie movie;
        final double score;
        final String reason;

        ScoredCandidate(Movie movie, double score, String reason) {
            this.movie = movie;
            this.score = score;
            this.reason = reason;
        }
    }
}
