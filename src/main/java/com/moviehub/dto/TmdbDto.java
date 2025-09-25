package com.moviehub.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class TmdbDto {
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MovieResponse {
        private Integer id;
        private String title;
        @JsonProperty("original_title")
        private String originalTitle;
        private String overview;
        @JsonProperty("release_date")
        private String releaseDate;
        @JsonProperty("poster_path")
        private String posterPath;
        @JsonProperty("backdrop_path")
        private String backdropPath;
        @JsonProperty("vote_average")
        private BigDecimal voteAverage;
        @JsonProperty("vote_count")
        private Integer voteCount;
        private BigDecimal popularity;
        private List<Genre> genres;
        private Integer runtime;
        private Boolean adult;
        @JsonProperty("original_language")
        private String originalLanguage;
        @JsonProperty("production_countries")
        private List<ProductionCountry> productionCountries;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Genre {
        private Integer id;
        private String name;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductionCountry {
        @JsonProperty("iso_3166_1")
        private String iso31661;
        private String name;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SearchResponse {
        private Integer page;
        private List<MovieResponse> results;
        @JsonProperty("total_pages")
        private Integer totalPages;
        @JsonProperty("total_results")
        private Integer totalResults;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TrendingResponse {
        private Integer page;
        private List<MovieResponse> results;
        @JsonProperty("total_pages")
        private Integer totalPages;
        @JsonProperty("total_results")
        private Integer totalResults;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PopularResponse {
        private Integer page;
        private List<MovieResponse> results;
        @JsonProperty("total_pages")
        private Integer totalPages;
        @JsonProperty("total_results")
        private Integer totalResults;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TopRatedResponse {
        private Integer page;
        private List<MovieResponse> results;
        @JsonProperty("total_pages")
        private Integer totalPages;
        @JsonProperty("total_results")
        private Integer totalResults;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GenreListResponse {
        private List<Genre> genres;
    }
}
