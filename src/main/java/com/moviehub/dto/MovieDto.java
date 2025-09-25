package com.moviehub.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
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
public class MovieDto {
    
    private UUID id;
    private Integer tmdbId;
    private String title;
    private String originalTitle;
    private String overview;
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate releaseDate;
    
    private String posterUrl;
    private String backdropUrl;
    private BigDecimal voteAverage;
    private Integer voteCount;
    private BigDecimal popularity;
    private List<String> genres;
    private Integer runtime;
    private Boolean adult;
    
    // Additional fields for API responses
    private Double averageRating;
    private Long reviewCount;
    private Boolean isFavorite;
    private Boolean isReviewed;
    private Integer userRating;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateRequest {
        private Integer tmdbId;
        private String title;
        private String originalTitle;
        private String overview;
        private LocalDate releaseDate;
        private String posterUrl;
        private String backdropUrl;
        private BigDecimal voteAverage;
        private Integer voteCount;
        private BigDecimal popularity;
        private List<String> genres;
        private Integer runtime;
        private Boolean adult;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateRequest {
        private String title;
        private String originalTitle;
        private String overview;
        private LocalDate releaseDate;
        private String posterUrl;
        private String backdropUrl;
        private BigDecimal voteAverage;
        private Integer voteCount;
        private BigDecimal popularity;
        private List<String> genres;
        private Integer runtime;
        private Boolean adult;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SearchRequest {
        private String query;
        private String genre;
        private Integer minRating;
        private Integer maxRating;
        private LocalDate fromDate;
        private LocalDate toDate;
        private Integer minRuntime;
        private Integer maxRuntime;
        private Boolean adult;
        private String sortBy; // popularity, vote_average, release_date
        private String sortOrder; // asc, desc
    }
}
