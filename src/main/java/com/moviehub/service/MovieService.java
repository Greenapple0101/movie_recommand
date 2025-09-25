package com.moviehub.service;

import com.moviehub.dto.MovieDto;
import com.moviehub.entity.Movie;
import com.moviehub.repository.MovieRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class MovieService {
    
    private final MovieRepository movieRepository;
    
    public Page<MovieDto> getAllMovies(Pageable pageable) {
        return movieRepository.findAll(pageable)
                .map(this::convertToDto);
    }
    
    public Optional<MovieDto> getMovieById(UUID id) {
        return movieRepository.findById(id)
                .map(this::convertToDto);
    }
    
    public Optional<MovieDto> getMovieByTmdbId(Integer tmdbId) {
        return movieRepository.findByTmdbId(tmdbId)
                .map(this::convertToDto);
    }
    
    @Cacheable(value = "movies", key = "#query + '_' + #pageable.pageNumber + '_' + #pageable.pageSize")
    public Page<MovieDto> searchMovies(String query, Pageable pageable) {
        log.info("Searching movies with query: {}", query);
        return movieRepository.findByTitleContainingIgnoreCase(query, pageable)
                .map(this::convertToDto);
    }
    
    @Cacheable(value = "trending", key = "#pageable.pageNumber + '_' + #pageable.pageSize")
    public List<MovieDto> getTrendingMovies(Pageable pageable) {
        LocalDate thirtyDaysAgo = LocalDate.now().minusDays(30);
        LocalDate now = LocalDate.now();
        
        return movieRepository.findTrendingMovies(thirtyDaysAgo, now)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    @Cacheable(value = "topRated", key = "#minRating + '_' + #pageable.pageNumber + '_' + #pageable.pageSize")
    public Page<MovieDto> getTopRatedMovies(Double minRating, Pageable pageable) {
        return movieRepository.findTopRatedMovies(minRating, pageable)
                .map(this::convertToDto);
    }
    
    @Cacheable(value = "moviesByGenre", key = "#genre + '_' + #pageable.pageNumber + '_' + #pageable.pageSize")
    public Page<MovieDto> getMoviesByGenre(String genre, Pageable pageable) {
        return movieRepository.findByGenre(genre, pageable)
                .map(this::convertToDto);
    }
    
    public Page<MovieDto> getRecentMovies(LocalDate fromDate, Pageable pageable) {
        return movieRepository.findRecentMovies(fromDate, pageable)
                .map(this::convertToDto);
    }
    
    public List<MovieDto> getFavoriteMoviesByUser(UUID userId) {
        return movieRepository.findFavoriteMoviesByUser(userId)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    public List<MovieDto> getReviewedMoviesByUser(UUID userId) {
        return movieRepository.findReviewedMoviesByUser(userId)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    @Cacheable(value = "familyFriendly", key = "#pageable.pageNumber + '_' + #pageable.pageSize")
    public Page<MovieDto> getFamilyFriendlyMovies(Pageable pageable) {
        return movieRepository.findPopularFamilyFriendlyMovies(pageable)
                .map(this::convertToDto);
    }
    
    public Page<MovieDto> getMoviesByRuntimeRange(Integer minRuntime, Integer maxRuntime, Pageable pageable) {
        return movieRepository.findByRuntimeRange(minRuntime, maxRuntime, pageable)
                .map(this::convertToDto);
    }
    
    @Transactional
    public MovieDto createMovie(MovieDto.CreateRequest request) {
        Movie movie = Movie.builder()
                .tmdbId(request.getTmdbId())
                .title(request.getTitle())
                .originalTitle(request.getOriginalTitle())
                .overview(request.getOverview())
                .releaseDate(request.getReleaseDate())
                .posterUrl(request.getPosterUrl())
                .backdropUrl(request.getBackdropUrl())
                .voteAverage(request.getVoteAverage())
                .voteCount(request.getVoteCount())
                .popularity(request.getPopularity())
                .genres(convertGenresToString(request.getGenres()))
                .runtime(request.getRuntime())
                .adult(request.getAdult())
                .build();
        
        Movie savedMovie = movieRepository.save(movie);
        return convertToDto(savedMovie);
    }
    
    @Transactional
    public Optional<MovieDto> updateMovie(UUID id, MovieDto.UpdateRequest request) {
        return movieRepository.findById(id)
                .map(movie -> {
                    movie.setTitle(request.getTitle());
                    movie.setOriginalTitle(request.getOriginalTitle());
                    movie.setOverview(request.getOverview());
                    movie.setReleaseDate(request.getReleaseDate());
                    movie.setPosterUrl(request.getPosterUrl());
                    movie.setBackdropUrl(request.getBackdropUrl());
                    movie.setVoteAverage(request.getVoteAverage());
                    movie.setVoteCount(request.getVoteCount());
                    movie.setPopularity(request.getPopularity());
                    movie.setGenres(convertGenresToString(request.getGenres()));
                    movie.setRuntime(request.getRuntime());
                    movie.setAdult(request.getAdult());
                    
                    return convertToDto(movieRepository.save(movie));
                });
    }
    
    @Transactional
    public boolean deleteMovie(UUID id) {
        if (movieRepository.existsById(id)) {
            movieRepository.deleteById(id);
            return true;
        }
        return false;
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
    
    private String convertGenresToString(List<String> genres) {
        if (genres == null || genres.isEmpty()) {
            return "[]";
        }
        return "[" + genres.stream()
                .map(genre -> "\"" + genre + "\"")
                .collect(Collectors.joining(",")) + "]";
    }
    
    private List<String> convertStringToGenres(String genresJson) {
        if (genresJson == null || genresJson.equals("[]")) {
            return List.of();
        }
        // Simple JSON parsing for genres array
        String cleanJson = genresJson.replaceAll("[\\[\\]\"]", "");
        if (cleanJson.isEmpty()) {
            return List.of();
        }
        return List.of(cleanJson.split(","));
    }
}
