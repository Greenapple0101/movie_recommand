package com.moviehub.dto;

import com.moviehub.entity.Review;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewDto {
    
    private UUID id;
    private UUID userId;
    private UUID movieId;
    private String username;
    private String movieTitle;
    private Integer rating;
    private String comment;
    private Review.SentimentType sentiment;
    private BigDecimal sentimentScore;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateRequest {
        private UUID movieId;
        private Integer rating;
        private String comment;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateRequest {
        private Integer rating;
        private String comment;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response {
        private UUID id;
        private String username;
        private Integer rating;
        private String comment;
        private Review.SentimentType sentiment;
        private BigDecimal sentimentScore;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MovieReviewSummary {
        private UUID movieId;
        private String movieTitle;
        private Double averageRating;
        private Long totalReviews;
        private Long positiveReviews;
        private Long negativeReviews;
        private Long neutralReviews;
    }
}
