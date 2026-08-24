package com.arvin.filmrec.dto.tmdb;

import lombok.Data;

import java.util.List;

@Data
public class TmdbDiscoverResponse {
    private Integer page;
    private List<TmdbMovieSummaryDto> results;

    @com.fasterxml.jackson.annotation.JsonProperty("total_pages")
    private Integer totalPages;
}
