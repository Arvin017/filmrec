package com.arvin.filmrec.service;

import com.arvin.filmrec.dto.tmdb.*;
import com.arvin.filmrec.entity.Actor;
import com.arvin.filmrec.entity.Director;
import com.arvin.filmrec.entity.Genre;
import com.arvin.filmrec.entity.Movie;
import com.arvin.filmrec.repository.ActorRepository;
import com.arvin.filmrec.repository.DirectorRepository;
import com.arvin.filmrec.repository.GenreRepository;
import com.arvin.filmrec.repository.MovieRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDate;

@Slf4j
@Service
@RequiredArgsConstructor
public class TmdbSeederService {

    private static final int CAST_LIMIT_PER_MOVIE = 5;

    @Value("${tmdb.api-key}")
    private String apiKey;

    @Value("${tmdb.base-url}")
    private String baseUrl;

    @Value("${tmdb.image-base-url}")
    private String imageBaseUrl;

    @Value("${tmdb.seed-page-count}")
    private int defaultPageCount;

    private final RestTemplate restTemplate;

    private final MovieRepository movieRepository;
    private final GenreRepository genreRepository;
    private final DirectorRepository directorRepository;
    private final ActorRepository actorRepository;

    private final PlatformTransactionManager transactionManager;

    @PersistenceContext
    private EntityManager entityManager;


    /**
     * Seeds movies from TMDB.
     *
     * IMPORTANT:
     * Each movie is processed in its own transaction.
     *
     * This prevents one huge transaction from holding MySQL locks
     * for hundreds of movies and greatly reduces deadlock risk.
     */
    public SeedResult seed(Integer pageCountOverride) {

        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException(
                    "TMDB_API_KEY is not configured. Set it as an env var before seeding."
            );
        }

        int pageCount =
                (pageCountOverride == null || pageCountOverride <= 0)
                        ? defaultPageCount
                        : pageCountOverride;

        log.info("==========================================");
        log.info("Starting TMDB seed. Pages={}", pageCount);
        log.info("Each movie will use its own transaction.");
        log.info("==========================================");

        // Genres can safely be seeded separately.
        seedGenres();

        int created = 0;
        int skipped = 0;

        for (int page = 1; page <= pageCount; page++) {

            log.info(
                    "Fetching TMDB popular movies page {}/{}",
                    page,
                    pageCount
            );

            TmdbDiscoverResponse response = fetchPopularPage(page);

            if (response == null || response.getResults() == null) {
                log.warn("No results returned for TMDB page {}", page);
                continue;
            }

            for (TmdbMovieSummaryDto summary : response.getResults()) {

                if (summary == null || summary.getId() == null) {
                    continue;
                }

                try {

                    Boolean wasCreated =
                            new TransactionTemplate(transactionManager)
                                    .execute(status ->
                                            seedSingleMovie(summary.getId())
                                    );

                    if (Boolean.TRUE.equals(wasCreated)) {
                        created++;
                    } else {
                        skipped++;
                    }

                } catch (Exception e) {

                    log.error(
                            "Failed to seed movie TMDB ID {}. Continuing with next movie. Error: {}",
                            summary.getId(),
                            e.getMessage()
                    );

                    // Important:
                    // One failed movie should NOT stop the entire seed.
                    skipped++;
                }
            }
        }

        log.info("==========================================");
        log.info(
                "TMDB seeding complete. Created={}, Skipped={}",
                created,
                skipped
        );
        log.info("==========================================");

        return new SeedResult(created, skipped);
    }


    /**
     * Seed all TMDB genres.
     *
     * Each repository save has its own transaction.
     */
    private void seedGenres() {

        String url = UriComponentsBuilder
                .fromHttpUrl(baseUrl + "/genre/movie/list")
                .queryParam("api_key", apiKey)
                .toUriString();

        try {

            TmdbGenreListResponse response =
                    restTemplate.getForObject(
                            url,
                            TmdbGenreListResponse.class
                    );

            if (response == null || response.getGenres() == null) {
                log.warn("TMDB returned no genres.");
                return;
            }

            for (TmdbGenreDto dto : response.getGenres()) {

                if (dto == null || dto.getId() == null) {
                    continue;
                }

                if (genreRepository.findByTmdbId(dto.getId()).isEmpty()) {

                    genreRepository.save(
                            Genre.builder()
                                    .tmdbId(dto.getId())
                                    .name(dto.getName())
                                    .build()
                    );

                    log.debug(
                            "Created genre: {}",
                            dto.getName()
                    );
                }
            }

        } catch (Exception e) {

            log.warn(
                    "Failed to seed TMDB genres: {}",
                    e.getMessage()
            );
        }
    }


    /**
     * Fetch one page of popular movies from TMDB.
     */
    private TmdbDiscoverResponse fetchPopularPage(int page) {

        String url = UriComponentsBuilder
                .fromHttpUrl(baseUrl + "/movie/popular")
                .queryParam("api_key", apiKey)
                .queryParam("page", page)
                .toUriString();

        try {

            return restTemplate.getForObject(
                    url,
                    TmdbDiscoverResponse.class
            );

        } catch (Exception e) {

            log.warn(
                    "Failed to fetch TMDB popular page {}: {}",
                    page,
                    e.getMessage()
            );

            return null;
        }
    }


    /**
     * Creates one movie and all of its relationships.
     *
     * This method is called inside a TransactionTemplate from seed().
     *
     * Therefore every movie gets its own short transaction.
     */
    private boolean seedSingleMovie(Integer tmdbId) {

        if (tmdbId == null) {
            return false;
        }

        /*
         * Check for duplicates.
         */
        if (movieRepository.findByTmdbId(tmdbId).isPresent()) {

            log.debug(
                    "Movie with TMDB ID {} already exists. Skipping.",
                    tmdbId
            );

            return false;
        }


        /*
         * Fetch movie details.
         */
        TmdbMovieDetailDto detail =
                fetchMovieDetail(tmdbId);

        if (detail == null) {
            return false;
        }


        /*
         * Fetch cast + crew.
         */
        TmdbCreditsResponse credits =
                fetchCredits(tmdbId);


        /*
         * Create Movie entity.
         */
        Movie movie = Movie.builder()
                .tmdbId(detail.getId())
                .title(detail.getTitle())
                .releaseYear(parseYear(detail.getReleaseDate()))
                .runtimeMinutes(detail.getRuntime())
                .overview(detail.getOverview())
                .posterUrl(
                        detail.getPosterPath() == null
                                ? null
                                : imageBaseUrl + detail.getPosterPath()
                )
                .tmdbRating(detail.getVoteAverage())
                .build();


        /*
         * ==========================
         * GENRES
         * ==========================
         */
        if (detail.getGenres() != null) {

            for (TmdbGenreDto genreDto : detail.getGenres()) {

                if (genreDto == null || genreDto.getId() == null) {
                    continue;
                }

                Genre genre =
                        findOrCreateGenre(
                                genreDto.getId(),
                                genreDto.getName()
                        );

                movie.getGenres().add(genre);
            }
        }


        /*
         * ==========================
         * DIRECTORS + ACTORS
         * ==========================
         */
        if (credits != null) {

            /*
             * DIRECTORS
             */
            if (credits.getCrew() != null) {

                credits.getCrew()
                        .stream()
                        .filter(c ->
                                c != null &&
                                        "Director".equalsIgnoreCase(c.getJob())
                        )
                        .forEach(c -> {

                            Director director =
                                    findOrCreateDirector(
                                            c.getId(),
                                            c.getName()
                                    );

                            movie.getDirectors().add(director);
                        });
            }


            /*
             * ACTORS
             *
             * Only top 5 cast members.
             */
            if (credits.getCast() != null) {

                credits.getCast()
                        .stream()
                        .filter(c -> c != null)
                        .sorted((a, b) -> {

                            int orderA =
                                    a.getOrder() == null
                                            ? Integer.MAX_VALUE
                                            : a.getOrder();

                            int orderB =
                                    b.getOrder() == null
                                            ? Integer.MAX_VALUE
                                            : b.getOrder();

                            return Integer.compare(
                                    orderA,
                                    orderB
                            );
                        })
                        .limit(CAST_LIMIT_PER_MOVIE)
                        .forEach(c -> {

                            Actor actor =
                                    findOrCreateActor(
                                            c.getId(),
                                            c.getName()
                                    );

                            movie.getActors().add(actor);
                        });
            }
        }


        /*
         * Save movie + relationships.
         */
        movieRepository.save(movie);

        /*
         * Force SQL execution before transaction ends.
         *
         * This makes errors happen for the current movie only,
         * rather than later during another movie.
         */
        entityManager.flush();

        log.info(
                "Created movie: {} (TMDB ID={})",
                movie.getTitle(),
                movie.getTmdbId()
        );

        return true;
    }


    /**
     * Find existing Genre or create a new one.
     *
     * Because this method runs inside the current movie transaction,
     * the returned entity is managed by the current persistence context.
     */
    private Genre findOrCreateGenre(
            Integer tmdbId,
            String name
    ) {

        return genreRepository
                .findByTmdbId(tmdbId)
                .orElseGet(() ->
                        genreRepository.save(
                                Genre.builder()
                                        .tmdbId(tmdbId)
                                        .name(name)
                                        .build()
                        )
                );
    }


    /**
     * Find existing Director or create a new one.
     *
     * No entityManager.merge() is required.
     */
    private Director findOrCreateDirector(
            Integer tmdbId,
            String name
    ) {

        return directorRepository
                .findByTmdbId(tmdbId)
                .orElseGet(() ->
                        directorRepository.save(
                                Director.builder()
                                        .tmdbId(tmdbId)
                                        .name(name)
                                        .build()
                        )
                );
    }


    /**
     * Find existing Actor or create a new one.
     *
     * No entityManager.merge() is required.
     */
    private Actor findOrCreateActor(
            Integer tmdbId,
            String name
    ) {

        return actorRepository
                .findByTmdbId(tmdbId)
                .orElseGet(() ->
                        actorRepository.save(
                                Actor.builder()
                                        .tmdbId(tmdbId)
                                        .name(name)
                                        .build()
                        )
                );
    }


    /**
     * Fetch detailed movie information from TMDB.
     */
    private TmdbMovieDetailDto fetchMovieDetail(Integer tmdbId) {

        String url = UriComponentsBuilder
                .fromHttpUrl(baseUrl + "/movie/" + tmdbId)
                .queryParam("api_key", apiKey)
                .toUriString();

        try {

            return restTemplate.getForObject(
                    url,
                    TmdbMovieDetailDto.class
            );

        } catch (Exception e) {

            log.warn(
                    "Failed to fetch TMDB movie detail for id={}: {}",
                    tmdbId,
                    e.getMessage()
            );

            return null;
        }
    }


    /**
     * Fetch cast and crew information from TMDB.
     */
    private TmdbCreditsResponse fetchCredits(Integer tmdbId) {

        String url = UriComponentsBuilder
                .fromHttpUrl(
                        baseUrl + "/movie/" + tmdbId + "/credits"
                )
                .queryParam("api_key", apiKey)
                .toUriString();

        try {

            return restTemplate.getForObject(
                    url,
                    TmdbCreditsResponse.class
            );

        } catch (Exception e) {

            log.warn(
                    "Failed to fetch TMDB credits for id={}: {}",
                    tmdbId,
                    e.getMessage()
            );

            return null;
        }
    }


    /**
     * Extract release year from TMDB yyyy-MM-dd date.
     */
    private Integer parseYear(String releaseDate) {

        if (releaseDate == null || releaseDate.isBlank()) {
            return null;
        }

        try {

            return LocalDate
                    .parse(releaseDate)
                    .getYear();

        } catch (Exception e) {

            return null;
        }
    }


    /**
     * Result returned to the admin endpoint.
     */
    public record SeedResult(
            int created,
            int skipped
    ) {
    }
}