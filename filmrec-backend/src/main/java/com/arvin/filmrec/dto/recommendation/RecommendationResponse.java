package com.arvin.filmrec.dto.recommendation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecommendationResponse {
    private Long movieId;
    private String title;
    private String posterUrl;
    private Integer releaseYear;
    private Double tmdbRating;
    private double score;
    private String reason;
}
