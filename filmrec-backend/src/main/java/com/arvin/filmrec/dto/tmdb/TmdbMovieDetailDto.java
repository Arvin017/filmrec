package com.arvin.filmrec.dto.tmdb;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/** Shape returned by /movie/{id} */
@Data
public class TmdbMovieDetailDto {
    private Integer id;
    private String title;

    @JsonProperty("release_date")
    private String releaseDate;

    private String overview;

    @JsonProperty("poster_path")
    private String posterPath;

    @JsonProperty("vote_average")
    private Double voteAverage;

    private Integer runtime;

    private List<TmdbGenreDto> genres;
}
