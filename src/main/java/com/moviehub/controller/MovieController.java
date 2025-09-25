package com.moviehub.controller;

import com.moviehub.dto.MovieDto;
import com.moviehub.service.MovieService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/movies")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Movie", description = "영화 관련 API")
public class MovieController {
    
    private final MovieService movieService;
    
    @GetMapping
    @Operation(summary = "모든 영화 조회", description = "페이징을 지원하는 영화 목록을 조회합니다.")
    public ResponseEntity<Page<MovieDto>> getAllMovies(
            @Parameter(description = "페이지 번호 (0부터 시작)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "정렬 기준") @RequestParam(defaultValue = "popularity") String sortBy,
            @Parameter(description = "정렬 방향") @RequestParam(defaultValue = "desc") String sortDir) {
        
        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<MovieDto> movies = movieService.getAllMovies(pageable);
        return ResponseEntity.ok(movies);
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "영화 상세 조회", description = "ID로 특정 영화의 상세 정보를 조회합니다.")
    public ResponseEntity<MovieDto> getMovieById(@PathVariable UUID id) {
        return movieService.getMovieById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/tmdb/{tmdbId}")
    @Operation(summary = "TMDb ID로 영화 조회", description = "TMDb ID로 영화 정보를 조회합니다.")
    public ResponseEntity<MovieDto> getMovieByTmdbId(@PathVariable Integer tmdbId) {
        return movieService.getMovieByTmdbId(tmdbId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/search")
    @Operation(summary = "영화 검색", description = "제목으로 영화를 검색합니다.")
    public ResponseEntity<Page<MovieDto>> searchMovies(
            @Parameter(description = "검색어") @RequestParam String query,
            @Parameter(description = "페이지 번호") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기") @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<MovieDto> movies = movieService.searchMovies(query, pageable);
        return ResponseEntity.ok(movies);
    }
    
    @GetMapping("/trending")
    @Operation(summary = "트렌딩 영화 조회", description = "최근 30일간 인기 있는 영화를 조회합니다.")
    public ResponseEntity<List<MovieDto>> getTrendingMovies(
            @Parameter(description = "페이지 번호") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기") @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        List<MovieDto> movies = movieService.getTrendingMovies(pageable);
        return ResponseEntity.ok(movies);
    }
    
    @GetMapping("/top-rated")
    @Operation(summary = "높은 평점 영화 조회", description = "높은 평점의 영화를 조회합니다.")
    public ResponseEntity<Page<MovieDto>> getTopRatedMovies(
            @Parameter(description = "최소 평점") @RequestParam(defaultValue = "7.0") Double minRating,
            @Parameter(description = "페이지 번호") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기") @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<MovieDto> movies = movieService.getTopRatedMovies(minRating, pageable);
        return ResponseEntity.ok(movies);
    }
    
    @GetMapping("/genre/{genre}")
    @Operation(summary = "장르별 영화 조회", description = "특정 장르의 영화를 조회합니다.")
    public ResponseEntity<Page<MovieDto>> getMoviesByGenre(
            @PathVariable String genre,
            @Parameter(description = "페이지 번호") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기") @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<MovieDto> movies = movieService.getMoviesByGenre(genre, pageable);
        return ResponseEntity.ok(movies);
    }
    
    @GetMapping("/recent")
    @Operation(summary = "최신 영화 조회", description = "최근 개봉한 영화를 조회합니다.")
    public ResponseEntity<Page<MovieDto>> getRecentMovies(
            @Parameter(description = "시작 날짜") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @Parameter(description = "페이지 번호") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기") @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<MovieDto> movies = movieService.getRecentMovies(fromDate, pageable);
        return ResponseEntity.ok(movies);
    }
    
    @GetMapping("/family-friendly")
    @Operation(summary = "가족 영화 조회", description = "가족이 함께 볼 수 있는 영화를 조회합니다.")
    public ResponseEntity<Page<MovieDto>> getFamilyFriendlyMovies(
            @Parameter(description = "페이지 번호") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기") @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<MovieDto> movies = movieService.getFamilyFriendlyMovies(pageable);
        return ResponseEntity.ok(movies);
    }
    
    @GetMapping("/runtime")
    @Operation(summary = "상영시간별 영화 조회", description = "상영시간 범위로 영화를 조회합니다.")
    public ResponseEntity<Page<MovieDto>> getMoviesByRuntimeRange(
            @Parameter(description = "최소 상영시간 (분)") @RequestParam Integer minRuntime,
            @Parameter(description = "최대 상영시간 (분)") @RequestParam Integer maxRuntime,
            @Parameter(description = "페이지 번호") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기") @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<MovieDto> movies = movieService.getMoviesByRuntimeRange(minRuntime, maxRuntime, pageable);
        return ResponseEntity.ok(movies);
    }
    
    @PostMapping
    @Operation(summary = "영화 생성", description = "새로운 영화를 생성합니다.")
    public ResponseEntity<MovieDto> createMovie(@Valid @RequestBody MovieDto.CreateRequest request) {
        MovieDto movie = movieService.createMovie(request);
        return ResponseEntity.ok(movie);
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "영화 수정", description = "기존 영화 정보를 수정합니다.")
    public ResponseEntity<MovieDto> updateMovie(
            @PathVariable UUID id,
            @Valid @RequestBody MovieDto.UpdateRequest request) {
        
        return movieService.updateMovie(id, request)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "영화 삭제", description = "영화를 삭제합니다.")
    public ResponseEntity<Void> deleteMovie(@PathVariable UUID id) {
        boolean deleted = movieService.deleteMovie(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}
