package com.moviehub.controller;

import com.moviehub.dto.FavoriteDto;
import com.moviehub.service.FavoriteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/favorites")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Favorite", description = "즐겨찾기 관련 API")
public class FavoriteController {
    
    private final FavoriteService favoriteService;
    
    @GetMapping("/user/{userId}")
    @Operation(summary = "사용자 즐겨찾기 조회", description = "특정 사용자의 모든 즐겨찾기를 조회합니다.")
    public ResponseEntity<List<FavoriteDto>> getFavoritesByUserId(@PathVariable UUID userId) {
        List<FavoriteDto> favorites = favoriteService.getFavoritesByUserId(userId);
        return ResponseEntity.ok(favorites);
    }
    
    @GetMapping("/movie/{movieId}")
    @Operation(summary = "영화 즐겨찾기 조회", description = "특정 영화를 즐겨찾기한 사용자들을 조회합니다.")
    public ResponseEntity<List<FavoriteDto>> getFavoritesByMovieId(@PathVariable UUID movieId) {
        List<FavoriteDto> favorites = favoriteService.getFavoritesByMovieId(movieId);
        return ResponseEntity.ok(favorites);
    }
    
    @GetMapping("/user/{userId}/recent")
    @Operation(summary = "사용자 최근 즐겨찾기 조회", description = "특정 사용자의 최근 즐겨찾기를 조회합니다.")
    public ResponseEntity<List<FavoriteDto>> getFavoritesByUserIdOrderByCreatedAt(@PathVariable UUID userId) {
        List<FavoriteDto> favorites = favoriteService.getFavoritesByUserIdOrderByCreatedAt(userId);
        return ResponseEntity.ok(favorites);
    }
    
    @GetMapping("/user/{userId}/movie/{movieId}")
    @Operation(summary = "사용자-영화 즐겨찾기 조회", description = "특정 사용자가 특정 영화를 즐겨찾기했는지 확인합니다.")
    public ResponseEntity<FavoriteDto> getFavoriteByUserAndMovie(
            @PathVariable UUID userId,
            @PathVariable UUID movieId) {
        
        return favoriteService.getFavoriteByUserAndMovie(userId, movieId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/movie/{movieId}/count")
    @Operation(summary = "영화 즐겨찾기 수 조회", description = "특정 영화의 즐겨찾기 수를 조회합니다.")
    public ResponseEntity<Long> getFavoriteCountByMovieId(@PathVariable UUID movieId) {
        Long count = favoriteService.getFavoriteCountByMovieId(movieId);
        return ResponseEntity.ok(count);
    }
    
    @GetMapping("/user/{userId}/movie/{movieId}/check")
    @Operation(summary = "즐겨찾기 여부 확인", description = "특정 사용자가 특정 영화를 즐겨찾기했는지 확인합니다.")
    public ResponseEntity<Boolean> isFavorite(
            @PathVariable UUID userId,
            @PathVariable UUID movieId) {
        
        boolean isFavorite = favoriteService.isFavorite(userId, movieId);
        return ResponseEntity.ok(isFavorite);
    }
    
    @PostMapping
    @Operation(summary = "즐겨찾기 추가", description = "영화를 즐겨찾기에 추가합니다.")
    public ResponseEntity<FavoriteDto> addFavorite(
            @Valid @RequestBody FavoriteDto.AddRequest request,
            Authentication authentication) {
        
        try {
            UUID userId = UUID.fromString(authentication.getName());
            FavoriteDto favorite = favoriteService.addFavorite(userId, request.getMovieId());
            return ResponseEntity.ok(favorite);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PostMapping("/toggle")
    @Operation(summary = "즐겨찾기 토글", description = "즐겨찾기를 추가하거나 제거합니다.")
    public ResponseEntity<Boolean> toggleFavorite(
            @Valid @RequestBody FavoriteDto.AddRequest request,
            Authentication authentication) {
        
        UUID userId = UUID.fromString(authentication.getName());
        boolean result = favoriteService.toggleFavorite(userId, request.getMovieId());
        return ResponseEntity.ok(result);
    }
    
    @DeleteMapping("/user/{userId}/movie/{movieId}")
    @Operation(summary = "즐겨찾기 제거", description = "즐겨찾기에서 영화를 제거합니다.")
    public ResponseEntity<Void> removeFavorite(
            @PathVariable UUID userId,
            @PathVariable UUID movieId,
            Authentication authentication) {
        
        // Check if the user is trying to remove their own favorite
        UUID currentUserId = UUID.fromString(authentication.getName());
        if (!currentUserId.equals(userId)) {
            return ResponseEntity.status(403).build();
        }
        
        boolean removed = favoriteService.removeFavorite(userId, movieId);
        return removed ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
    
    @DeleteMapping("/movie/{movieId}")
    @Operation(summary = "내 즐겨찾기 제거", description = "현재 사용자의 즐겨찾기에서 영화를 제거합니다.")
    public ResponseEntity<Void> removeMyFavorite(
            @PathVariable UUID movieId,
            Authentication authentication) {
        
        UUID userId = UUID.fromString(authentication.getName());
        boolean removed = favoriteService.removeFavorite(userId, movieId);
        return removed ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}
