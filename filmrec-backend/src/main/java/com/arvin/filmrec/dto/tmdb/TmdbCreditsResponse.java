package com.arvin.filmrec.dto.tmdb;

import lombok.Data;

import java.util.List;

/** Shape returned by /movie/{id}/credits */
@Data
public class TmdbCreditsResponse {
    private Integer id;
    private List<TmdbCastMemberDto> cast;
    private List<TmdbCrewMemberDto> crew;
}
