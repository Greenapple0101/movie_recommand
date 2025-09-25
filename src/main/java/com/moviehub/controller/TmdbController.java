package com.moviehub.controller;

import com.moviehub.dto.MovieDto;
import com.moviehub.dto.TmdbDto;
import com.moviehub.service.TmdbService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tmdb")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "TMDb Integration", description = "TMDb API 연동 관련 API")
public class TmdbController {
    
    private final TmdbService tmdbService;
    
    @GetMapping("/search")
    @Operation(summary = "TMDb 영화 검색", description = "TMDb API를 통해 영화를 검색합니다.")
    public ResponseEntity<TmdbDto.SearchResponse> searchMovies(
            @Parameter(description = "검색어") @RequestParam String query,
            @Parameter(description = "페이지 번호") @RequestParam(defaultValue = "1") Integer page) {
        
        try {
            TmdbDto.SearchResponse response = tmdbService.searchMovies(query, page);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/trending")
    @Operation(summary = "TMDb 트렌딩 영화", description = "TMDb API를 통해 트렌딩 영화를 조회합니다.")
    public ResponseEntity<TmdbDto.TrendingResponse> getTrendingMovies(
            @Parameter(description = "시간 범위 (day, week)") @RequestParam(defaultValue = "day") String timeWindow,
            @Parameter(description = "페이지 번호") @RequestParam(defaultValue = "1") Integer page) {
        
        try {
            TmdbDto.TrendingResponse response = tmdbService.getTrendingMovies(timeWindow, page);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/popular")
    @Operation(summary = "TMDb 인기 영화", description = "TMDb API를 통해 인기 영화를 조회합니다.")
    public ResponseEntity<TmdbDto.PopularResponse> getPopularMovies(
            @Parameter(description = "페이지 번호") @RequestParam(defaultValue = "1") Integer page) {
        
        try {
            TmdbDto.PopularResponse response = tmdbService.getPopularMovies(page);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/top-rated")
    @Operation(summary = "TMDb 높은 평점 영화", description = "TMDb API를 통해 높은 평점 영화를 조회합니다.")
    public ResponseEntity<TmdbDto.TopRatedResponse> getTopRatedMovies(
            @Parameter(description = "페이지 번호") @RequestParam(defaultValue = "1") Integer page) {
        
        try {
            TmdbDto.TopRatedResponse response = tmdbService.getTopRatedMovies(page);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/movie/{movieId}")
    @Operation(summary = "TMDb 영화 상세", description = "TMDb API를 통해 영화 상세 정보를 조회합니다.")
    public ResponseEntity<TmdbDto.MovieResponse> getMovieDetails(@PathVariable Integer movieId) {
        try {
            return tmdbService.getMovieDetails(movieId)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/genres")
    @Operation(summary = "TMDb 장르 목록", description = "TMDb API를 통해 영화 장르 목록을 조회합니다.")
    public ResponseEntity<List<TmdbDto.Genre>> getGenres() {
        try {
            List<TmdbDto.Genre> genres = tmdbService.getGenres();
            return ResponseEntity.ok(genres);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PostMapping("/sync/trending")
    @Operation(summary = "트렌딩 영화 동기화", description = "TMDb 트렌딩 영화를 로컬 DB에 동기화합니다.")
    public ResponseEntity<List<MovieDto>> syncTrendingMovies(
            @Parameter(description = "시간 범위 (day, week)") @RequestParam(defaultValue = "day") String timeWindow,
            @Parameter(description = "페이지 번호") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "동기화할 영화 수") @RequestParam(defaultValue = "20") Integer limit) {
        
        try {
            List<MovieDto> movies = tmdbService.syncMoviesFromTmdb(timeWindow, page, limit);
            return ResponseEntity.ok(movies);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PostMapping("/sync/search")
    @Operation(summary = "검색 영화 동기화", description = "TMDb 검색 결과를 로컬 DB에 동기화합니다.")
    public ResponseEntity<List<MovieDto>> syncSearchMovies(
            @Parameter(description = "검색어") @RequestParam String query,
            @Parameter(description = "페이지 번호") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "동기화할 영화 수") @RequestParam(defaultValue = "20") Integer limit) {
        
        try {
            List<MovieDto> movies = tmdbService.searchAndSyncMovies(query, page, limit);
            return ResponseEntity.ok(movies);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
