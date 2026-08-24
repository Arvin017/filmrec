package com.arvin.filmrec.dto.movie;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MovieSummaryResponse {
    private Long id;
    private String title;
    private Integer releaseYear;
    private String posterUrl;
    private Double tmdbRating;
    private Integer userRating; // null if current user hasn't rated it
}
