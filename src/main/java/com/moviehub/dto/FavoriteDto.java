package com.moviehub.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FavoriteDto {
    
    private UUID id;
    private UUID userId;
    private UUID movieId;
    private String username;
    private String movieTitle;
    private String moviePosterUrl;
    private LocalDateTime createdAt;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AddRequest {
        private UUID movieId;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response {
        private UUID id;
        private String username;
        private String movieTitle;
        private String moviePosterUrl;
        private LocalDateTime createdAt;
    }
}
