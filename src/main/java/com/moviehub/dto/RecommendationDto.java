package com.moviehub.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecommendationDto {
    
    private UUID movieId;
    private String title;
    private String originalTitle;
    private String overview;
    private LocalDate releaseDate;
    private String posterUrl;
    private BigDecimal voteAverage;
    private Integer voteCount;
    private List<String> genres;
    private Integer runtime;
    private String recommendationReason;
    private Double confidenceScore;
    private String recommendationType; // content_based, social_based, trending, mood_based
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MoodRecommendationRequest {
        private String mood; // happy, sad, excited, relaxed, romantic, action, comedy, etc.
        private Integer limit;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ContentBasedRequest {
        private UUID movieId;
        private Integer limit;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SocialBasedRequest {
        private UUID userId;
        private Integer limit;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TrendingRequest {
        private String timeRange; // day, week, month
        private Integer limit;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserProfileResponse {
        private UUID userId;
        private String username;
        private List<GenrePreference> genrePreferences;
        private List<MoodPreference> moodPreferences;
        private RatingPattern ratingPattern;
        private List<String> favoriteGenres;
        private List<String> favoriteMoods;
        private Double averageRating;
        private Integer totalReviews;
        private Integer totalFavorites;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GenrePreference {
        private String genre;
        private Double preferenceScore;
        private Integer movieCount;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MoodPreference {
        private String mood;
        private Double preferenceScore;
        private Integer movieCount;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RatingPattern {
        private Double averageRating;
        private Integer totalRatings;
        private List<RatingDistribution> distribution;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RatingDistribution {
        private Integer rating;
        private Integer count;
        private Double percentage;
    }
}
