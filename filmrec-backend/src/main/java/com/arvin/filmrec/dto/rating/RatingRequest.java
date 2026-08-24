package com.arvin.filmrec.dto.rating;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RatingRequest {

    @NotNull
    private Long movieId;

    @NotNull
    @Min(1)
    @Max(5)
    private Integer score;
}
