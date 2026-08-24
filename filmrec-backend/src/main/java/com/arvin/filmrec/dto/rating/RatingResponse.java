package com.arvin.filmrec.dto.rating;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RatingResponse {
    private Long id;
    private Long movieId;
    private String movieTitle;
    private Integer score;
    private LocalDateTime ratedAt;
}
