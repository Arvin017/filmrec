package com.arvin.filmrec.dto.movie;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MovieDetailResponse {
    private Long id;
    private String title;
    private Integer releaseYear;
    private Integer runtimeMinutes;
    private String overview;
    private String posterUrl;
    private Double tmdbRating;
    private List<String> genres;
    private List<String> directors;
    private List<String> actors;
    private Integer userRating;
}
