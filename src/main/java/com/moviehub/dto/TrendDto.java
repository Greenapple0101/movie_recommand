package com.moviehub.dto;

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
public class TrendDto {
    
    private UUID id;
    private String keyword;
    private String source;
    private BigDecimal score;
    private String metadata;
    private LocalDateTime createdAt;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateRequest {
        private String keyword;
        private String source;
        private BigDecimal score;
        private String metadata;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateRequest {
        private String keyword;
        private String source;
        private BigDecimal score;
        private String metadata;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TrendingKeyword {
        private String keyword;
        private Double averageScore;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TrendSummary {
        private String keyword;
        private String source;
        private BigDecimal score;
        private LocalDateTime lastUpdated;
        private String metadata;
    }
}
