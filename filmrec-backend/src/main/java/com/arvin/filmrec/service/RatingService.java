package com.arvin.filmrec.service;

import com.arvin.filmrec.dto.rating.RatingRequest;
import com.arvin.filmrec.dto.rating.RatingResponse;
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

@Service
@RequiredArgsConstructor
public class RatingService {

    private final RatingRepository ratingRepository;
    private final MovieRepository movieRepository;
    private final CurrentUserProvider currentUserProvider;

    @Transactional
    public RatingResponse rateMovie(RatingRequest request) {
        User user = currentUserProvider.getRequiredCurrentUser();

        Movie movie = movieRepository.findById(request.getMovieId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Movie not found: " + request.getMovieId()
                        ));

        Rating rating = ratingRepository
                .findByUserIdAndMovieId(user.getId(), movie.getId())
                .orElse(Rating.builder()
                        .user(user)
                        .movie(movie)
                        .build());

        rating.setScore(request.getScore());
        rating.setRatedAt(java.time.LocalDateTime.now());

        Rating saved = ratingRepository.save(rating);

        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<RatingResponse> getMyRatings() {
        User user = currentUserProvider.getRequiredCurrentUser();

        return ratingRepository.findByUserId(user.getId())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public void deleteRating(Long movieId) {
        User user = currentUserProvider.getRequiredCurrentUser();

        if (!ratingRepository.existsByUserIdAndMovieId(
                user.getId(),
                movieId
        )) {
            throw new ResourceNotFoundException(
                    "No rating found for movie " + movieId
            );
        }

        ratingRepository.deleteByUserIdAndMovieId(
                user.getId(),
                movieId
        );
    }

    private RatingResponse toResponse(Rating rating) {
        return RatingResponse.builder()
                .id(rating.getId())
                .movieId(rating.getMovie().getId())
                .movieTitle(rating.getMovie().getTitle())
                .score(rating.getScore())
                .ratedAt(rating.getRatedAt())
                .build();
    }
}