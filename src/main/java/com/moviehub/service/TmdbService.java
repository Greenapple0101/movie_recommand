package com.moviehub.service;

import com.moviehub.dto.MovieDto;
import com.moviehub.dto.TmdbDto;
import com.moviehub.entity.Movie;
import com.moviehub.repository.MovieRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class TmdbService {
    
    private final WebClient webClient;
    private final MovieRepository movieRepository;
    
    @Autowired
    public TmdbService(WebClient webClient, MovieRepository movieRepository) {
        this.webClient = webClient;
        this.movieRepository = movieRepository;
    }
    
    @Value("${external.tmdb.api-key}")
    private String apiKey;
    
    @Value("${external.tmdb.base-url}")
    private String baseUrl;
    
    @Value("${external.tmdb.image-base-url}")
    private String imageBaseUrl;
    
    public TmdbService(WebClient.Builder webClientBuilder, MovieRepository movieRepository) {
        this.webClient = webClientBuilder.build();
        this.movieRepository = movieRepository;
    }
    
    @Cacheable(value = "tmdbSearch", key = "#query + '_' + #page")
    public TmdbDto.SearchResponse searchMovies(String query, Integer page) {
        try {
            return webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/search/movie")
                            .queryParam("api_key", apiKey)
                            .queryParam("query", query)
                            .queryParam("page", page)
                            .queryParam("language", "ko-KR")
                            .build())
                    .retrieve()
                    .bodyToMono(TmdbDto.SearchResponse.class)
                    .block();
        } catch (WebClientResponseException e) {
            log.error("TMDb API search error: {}", e.getMessage());
            throw new RuntimeException("TMDb API 호출 중 오류가 발생했습니다.", e);
        }
    }
    
    @Cacheable(value = "tmdbTrending", key = "#timeWindow + '_' + #page")
    public TmdbDto.TrendingResponse getTrendingMovies(String timeWindow, Integer page) {
        try {
            return webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/trending/movie/{time_window}")
                            .queryParam("api_key", apiKey)
                            .queryParam("page", page)
                            .queryParam("language", "ko-KR")
                            .build(timeWindow))
                    .retrieve()
                    .bodyToMono(TmdbDto.TrendingResponse.class)
                    .block();
        } catch (WebClientResponseException e) {
            log.error("TMDb API trending error: {}", e.getMessage());
            throw new RuntimeException("TMDb API 호출 중 오류가 발생했습니다.", e);
        }
    }
    
    @Cacheable(value = "tmdbPopular", key = "#page")
    public TmdbDto.PopularResponse getPopularMovies(Integer page) {
        try {
            return webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/movie/popular")
                            .queryParam("api_key", apiKey)
                            .queryParam("page", page)
                            .queryParam("language", "ko-KR")
                            .build())
                    .retrieve()
                    .bodyToMono(TmdbDto.PopularResponse.class)
                    .block();
        } catch (WebClientResponseException e) {
            log.error("TMDb API popular error: {}", e.getMessage());
            throw new RuntimeException("TMDb API 호출 중 오류가 발생했습니다.", e);
        }
    }
    
    @Cacheable(value = "tmdbTopRated", key = "#page")
    public TmdbDto.TopRatedResponse getTopRatedMovies(Integer page) {
        try {
            return webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/movie/top_rated")
                            .queryParam("api_key", apiKey)
                            .queryParam("page", page)
                            .queryParam("language", "ko-KR")
                            .build())
                    .retrieve()
                    .bodyToMono(TmdbDto.TopRatedResponse.class)
                    .block();
        } catch (WebClientResponseException e) {
            log.error("TMDb API top rated error: {}", e.getMessage());
            throw new RuntimeException("TMDb API 호출 중 오류가 발생했습니다.", e);
        }
    }
    
    @Cacheable(value = "tmdbMovie", key = "#movieId")
    public Optional<TmdbDto.MovieResponse> getMovieDetails(Integer movieId) {
        try {
            TmdbDto.MovieResponse response = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/movie/{movie_id}")
                            .queryParam("api_key", apiKey)
                            .queryParam("language", "ko-KR")
                            .build(movieId))
                    .retrieve()
                    .bodyToMono(TmdbDto.MovieResponse.class)
                    .block();
            
            return Optional.ofNullable(response);
        } catch (WebClientResponseException e) {
            log.error("TMDb API movie details error: {}", e.getMessage());
            return Optional.empty();
        }
    }
    
    @Cacheable(value = "tmdbGenres")
    public List<TmdbDto.Genre> getGenres() {
        try {
            TmdbDto.GenreListResponse response = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/genre/movie/list")
                            .queryParam("api_key", apiKey)
                            .queryParam("language", "ko-KR")
                            .build())
                    .retrieve()
                    .bodyToMono(TmdbDto.GenreListResponse.class)
                    .block();
            
            return response != null ? response.getGenres() : List.of();
        } catch (WebClientResponseException e) {
            log.error("TMDb API genres error: {}", e.getMessage());
            return List.of();
        }
    }
    
    public List<MovieDto> syncMoviesFromTmdb(String timeWindow, Integer page, Integer limit) {
        TmdbDto.TrendingResponse response = getTrendingMovies(timeWindow, page);
        
        if (response == null || response.getResults() == null) {
            return List.of();
        }
        
        return response.getResults().stream()
                .limit(limit)
                .map(this::convertAndSaveMovie)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }
    
    public List<MovieDto> searchAndSyncMovies(String query, Integer page, Integer limit) {
        TmdbDto.SearchResponse response = searchMovies(query, page);
        
        if (response == null || response.getResults() == null) {
            return List.of();
        }
        
        return response.getResults().stream()
                .limit(limit)
                .map(this::convertAndSaveMovie)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }
    
    private Optional<MovieDto> convertAndSaveMovie(TmdbDto.MovieResponse tmdbMovie) {
        try {
            // Check if movie already exists
            Optional<Movie> existingMovie = movieRepository.findByTmdbId(tmdbMovie.getId());
            if (existingMovie.isPresent()) {
                return existingMovie.map(this::convertToDto);
            }
            
            // Convert and save new movie
            Movie movie = convertTmdbToEntity(tmdbMovie);
            Movie savedMovie = movieRepository.save(movie);
            return Optional.of(convertToDto(savedMovie));
        } catch (Exception e) {
            log.error("Error converting and saving movie: {}", e.getMessage());
            return Optional.empty();
        }
    }
    
    private Movie convertTmdbToEntity(TmdbDto.MovieResponse tmdbMovie) {
        LocalDate releaseDate = null;
        if (tmdbMovie.getReleaseDate() != null && !tmdbMovie.getReleaseDate().isEmpty()) {
            try {
                releaseDate = LocalDate.parse(tmdbMovie.getReleaseDate(), DateTimeFormatter.ISO_LOCAL_DATE);
            } catch (Exception e) {
                log.warn("Failed to parse release date: {}", tmdbMovie.getReleaseDate());
            }
        }
        
        String genresJson = "[]";
        if (tmdbMovie.getGenres() != null && !tmdbMovie.getGenres().isEmpty()) {
            genresJson = "[" + tmdbMovie.getGenres().stream()
                    .map(genre -> "\"" + genre.getName() + "\"")
                    .collect(Collectors.joining(",")) + "]";
        }
        
        return Movie.builder()
                .tmdbId(tmdbMovie.getId())
                .title(tmdbMovie.getTitle())
                .originalTitle(tmdbMovie.getOriginalTitle())
                .overview(tmdbMovie.getOverview())
                .releaseDate(releaseDate)
                .posterUrl(tmdbMovie.getPosterPath() != null ? 
                        imageBaseUrl + tmdbMovie.getPosterPath() : null)
                .backdropUrl(tmdbMovie.getBackdropPath() != null ? 
                        imageBaseUrl + tmdbMovie.getBackdropPath() : null)
                .voteAverage(tmdbMovie.getVoteAverage())
                .voteCount(tmdbMovie.getVoteCount())
                .popularity(tmdbMovie.getPopularity())
                .genres(genresJson)
                .runtime(tmdbMovie.getRuntime())
                .adult(tmdbMovie.getAdult())
                .build();
    }
    
    private MovieDto convertToDto(Movie movie) {
        return MovieDto.builder()
                .id(movie.getId())
                .tmdbId(movie.getTmdbId())
                .title(movie.getTitle())
                .originalTitle(movie.getOriginalTitle())
                .overview(movie.getOverview())
                .releaseDate(movie.getReleaseDate())
                .posterUrl(movie.getPosterUrl())
                .backdropUrl(movie.getBackdropUrl())
                .voteAverage(movie.getVoteAverage())
                .voteCount(movie.getVoteCount())
                .popularity(movie.getPopularity())
                .genres(convertStringToGenres(movie.getGenres()))
                .runtime(movie.getRuntime())
                .adult(movie.getAdult())
                .build();
    }
    
    private List<String> convertStringToGenres(String genresJson) {
        if (genresJson == null || genresJson.equals("[]")) {
            return List.of();
        }
        String cleanJson = genresJson.replaceAll("[\\[\\]\"]", "");
        if (cleanJson.isEmpty()) {
            return List.of();
        }
        return List.of(cleanJson.split(","));
    }
}
