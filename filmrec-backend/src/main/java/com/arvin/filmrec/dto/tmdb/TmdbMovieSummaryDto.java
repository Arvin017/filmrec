package com.arvin.filmrec.dto.tmdb;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/** Shape returned by /discover/movie and /movie/popular list endpoints. */
@Data
public class TmdbMovieSummaryDto {
    private Integer id;
    private String title;

    @JsonProperty("release_date")
    private String releaseDate;

    private String overview;

    @JsonProperty("poster_path")
    private String posterPath;

    @JsonProperty("vote_average")
    private Double voteAverage;

    @JsonProperty("genre_ids")
    private List<Integer> genreIds;
}
