package com.arvin.filmrec.service;

import com.arvin.filmrec.dto.movie.MovieDetailResponse;
import com.arvin.filmrec.dto.movie.MovieSummaryResponse;
import com.arvin.filmrec.entity.Movie;
import com.arvin.filmrec.entity.Rating;
import com.arvin.filmrec.entity.User;
import com.arvin.filmrec.exception.ResourceNotFoundException;
import com.arvin.filmrec.repository.MovieRepository;
import com.arvin.filmrec.repository.RatingRepository;
import com.arvin.filmrec.security.CurrentUserProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MovieService {

    private final MovieRepository movieRepository;
    private final RatingRepository ratingRepository;
    private final CurrentUserProvider currentUserProvider;

    public List<MovieSummaryResponse> getAllMovies() {
        return movieRepository.findAll().stream()
                .map(this::toSummary)
                .toList();
    }

    public List<MovieSummaryResponse> searchMovies(String query) {
        if (query == null || query.isBlank()) {
            return getAllMovies();
        }
        return movieRepository.search(query.trim()).stream()
                .map(this::toSummary)
                .toList();
    }

    public MovieDetailResponse getMovieDetail(Long id) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Movie not found: " + id));
        return toDetail(movie);
    }

    private MovieSummaryResponse toSummary(Movie movie) {
        Integer userRating = currentUserRatingFor(movie.getId());
        return MovieSummaryResponse.builder()
                .id(movie.getId())
                .title(movie.getTitle())
                .releaseYear(movie.getReleaseYear())
                .posterUrl(movie.getPosterUrl())
                .tmdbRating(movie.getTmdbRating())
                .userRating(userRating)
                .build();
    }

    private MovieDetailResponse toDetail(Movie movie) {
        Integer userRating = currentUserRatingFor(movie.getId());
        return MovieDetailResponse.builder()
                .id(movie.getId())
                .title(movie.getTitle())
                .releaseYear(movie.getReleaseYear())
                .runtimeMinutes(movie.getRuntimeMinutes())
                .overview(movie.getOverview())
                .posterUrl(movie.getPosterUrl())
                .tmdbRating(movie.getTmdbRating())
                .genres(movie.getGenres().stream().map(g -> g.getName()).toList())
                .directors(movie.getDirectors().stream().map(d -> d.getName()).toList())
                .actors(movie.getActors().stream().map(a -> a.getName()).toList())
                .userRating(userRating)
                .build();
    }

    private Integer currentUserRatingFor(Long movieId) {
        User user = currentUserProvider.getCurrentUserOrNull();
        if (user == null) {
            return null;
        }
        Optional<Rating> rating = ratingRepository.findByUserIdAndMovieId(user.getId(), movieId);
        return rating.map(Rating::getScore).orElse(null);
    }
}
