package com.moviehub.controller;

import com.moviehub.dto.ReviewDto;
import com.moviehub.entity.Review;
import com.moviehub.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Review", description = "리뷰 관련 API")
public class ReviewController {
    
    private final ReviewService reviewService;
    
    @GetMapping("/movie/{movieId}")
    @Operation(summary = "영화별 리뷰 조회", description = "특정 영화의 모든 리뷰를 조회합니다.")
    public ResponseEntity<List<ReviewDto>> getReviewsByMovieId(@PathVariable UUID movieId) {
        List<ReviewDto> reviews = reviewService.getReviewsByMovieId(movieId);
        return ResponseEntity.ok(reviews);
    }
    
    @GetMapping("/movie/{movieId}/paged")
    @Operation(summary = "영화별 리뷰 조회 (페이징)", description = "특정 영화의 리뷰를 페이징으로 조회합니다.")
    public ResponseEntity<Page<ReviewDto>> getReviewsByMovieIdPaged(
            @PathVariable UUID movieId,
            @Parameter(description = "페이지 번호") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기") @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<ReviewDto> reviews = reviewService.getReviewsByMovieIdPaged(movieId, pageable);
        return ResponseEntity.ok(reviews);
    }
    
    @GetMapping("/user/{userId}")
    @Operation(summary = "사용자별 리뷰 조회", description = "특정 사용자의 모든 리뷰를 조회합니다.")
    public ResponseEntity<List<ReviewDto>> getReviewsByUserId(@PathVariable UUID userId) {
        List<ReviewDto> reviews = reviewService.getReviewsByUserId(userId);
        return ResponseEntity.ok(reviews);
    }
    
    @GetMapping("/user/{userId}/paged")
    @Operation(summary = "사용자별 리뷰 조회 (페이징)", description = "특정 사용자의 리뷰를 페이징으로 조회합니다.")
    public ResponseEntity<Page<ReviewDto>> getReviewsByUserIdPaged(
            @PathVariable UUID userId,
            @Parameter(description = "페이지 번호") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기") @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<ReviewDto> reviews = reviewService.getReviewsByUserIdPaged(userId, pageable);
        return ResponseEntity.ok(reviews);
    }
    
    @GetMapping("/user/{userId}/movie/{movieId}")
    @Operation(summary = "사용자-영화 리뷰 조회", description = "특정 사용자가 특정 영화에 작성한 리뷰를 조회합니다.")
    public ResponseEntity<ReviewDto> getReviewByUserAndMovie(
            @PathVariable UUID userId,
            @PathVariable UUID movieId) {
        
        return reviewService.getReviewByUserAndMovie(userId, movieId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/movie/{movieId}/summary")
    @Operation(summary = "영화 리뷰 요약", description = "특정 영화의 리뷰 통계를 조회합니다.")
    public ResponseEntity<ReviewDto.MovieReviewSummary> getMovieReviewSummary(@PathVariable UUID movieId) {
        try {
            ReviewDto.MovieReviewSummary summary = reviewService.getMovieReviewSummary(movieId);
            return ResponseEntity.ok(summary);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/high-rated")
    @Operation(summary = "높은 평점 리뷰 조회", description = "높은 평점의 리뷰들을 조회합니다.")
    public ResponseEntity<Page<ReviewDto>> getHighRatedReviews(
            @Parameter(description = "최소 평점") @RequestParam(defaultValue = "8") Integer minRating,
            @Parameter(description = "페이지 번호") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기") @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<ReviewDto> reviews = reviewService.getHighRatedReviews(minRating, pageable);
        return ResponseEntity.ok(reviews);
    }
    
    @GetMapping("/sentiment/{sentiment}")
    @Operation(summary = "감정별 리뷰 조회", description = "특정 감정의 리뷰들을 조회합니다.")
    public ResponseEntity<Page<ReviewDto>> getReviewsBySentiment(
            @PathVariable Review.SentimentType sentiment,
            @Parameter(description = "페이지 번호") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기") @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<ReviewDto> reviews = reviewService.getReviewsBySentiment(sentiment, pageable);
        return ResponseEntity.ok(reviews);
    }
    
    @GetMapping("/with-comments")
    @Operation(summary = "댓글 있는 리뷰 조회", description = "댓글이 있는 리뷰들을 조회합니다.")
    public ResponseEntity<Page<ReviewDto>> getReviewsWithComments(
            @Parameter(description = "페이지 번호") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기") @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<ReviewDto> reviews = reviewService.getReviewsWithComments(pageable);
        return ResponseEntity.ok(reviews);
    }
    
    @PostMapping
    @Operation(summary = "리뷰 작성", description = "새로운 리뷰를 작성합니다.")
    public ResponseEntity<ReviewDto> createReview(
            @Valid @RequestBody ReviewDto.CreateRequest request,
            Authentication authentication) {
        
        try {
            // Get user ID from authentication
            UUID userId = UUID.fromString(authentication.getName());
            ReviewDto review = reviewService.createReview(userId, request);
            return ResponseEntity.ok(review);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PutMapping("/{reviewId}")
    @Operation(summary = "리뷰 수정", description = "기존 리뷰를 수정합니다.")
    public ResponseEntity<ReviewDto> updateReview(
            @PathVariable UUID reviewId,
            @Valid @RequestBody ReviewDto.UpdateRequest request,
            Authentication authentication) {
        
        UUID userId = UUID.fromString(authentication.getName());
        return reviewService.updateReview(userId, reviewId, request)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @DeleteMapping("/{reviewId}")
    @Operation(summary = "리뷰 삭제", description = "리뷰를 삭제합니다.")
    public ResponseEntity<Void> deleteReview(
            @PathVariable UUID reviewId,
            Authentication authentication) {
        
        UUID userId = UUID.fromString(authentication.getName());
        boolean deleted = reviewService.deleteReview(userId, reviewId);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}
