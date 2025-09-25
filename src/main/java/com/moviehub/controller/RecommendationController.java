package com.moviehub.controller;

import com.moviehub.dto.RecommendationDto;
import com.moviehub.service.RecommendationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/recommendations")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Recommendation", description = "추천 시스템 관련 API")
public class RecommendationController {
    
    private final RecommendationService recommendationService;
    
    @GetMapping("/content-based")
    @Operation(summary = "콘텐츠 기반 추천", description = "특정 영화와 유사한 영화를 추천합니다.")
    public ResponseEntity<List<RecommendationDto>> getContentBasedRecommendations(
            @Parameter(description = "기준 영화 ID") @RequestParam UUID movieId,
            @Parameter(description = "추천 개수") @RequestParam(defaultValue = "10") Integer limit) {
        
        try {
            List<RecommendationDto> recommendations = recommendationService.getContentBasedRecommendations(movieId, limit);
            return ResponseEntity.ok(recommendations);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/social-based")
    @Operation(summary = "소셜 기반 추천", description = "비슷한 취향의 사용자들이 좋아한 영화를 추천합니다.")
    public ResponseEntity<List<RecommendationDto>> getSocialBasedRecommendations(
            @Parameter(description = "사용자 ID") @RequestParam UUID userId,
            @Parameter(description = "추천 개수") @RequestParam(defaultValue = "10") Integer limit) {
        
        try {
            List<RecommendationDto> recommendations = recommendationService.getSocialBasedRecommendations(userId, limit);
            return ResponseEntity.ok(recommendations);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/trending")
    @Operation(summary = "트렌딩 추천", description = "현재 인기 있는 영화를 추천합니다.")
    public ResponseEntity<List<RecommendationDto>> getTrendingRecommendations(
            @Parameter(description = "시간 범위 (day, week, month)") @RequestParam(defaultValue = "day") String timeRange,
            @Parameter(description = "추천 개수") @RequestParam(defaultValue = "10") Integer limit) {
        
        try {
            List<RecommendationDto> recommendations = recommendationService.getTrendingRecommendations(timeRange, limit);
            return ResponseEntity.ok(recommendations);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/mood")
    @Operation(summary = "감정 기반 추천", description = "사용자의 기분에 맞는 영화를 추천합니다.")
    public ResponseEntity<List<RecommendationDto>> getMoodBasedRecommendations(
            @Parameter(description = "기분 (happy, sad, excited, relaxed, romantic, scared, adventurous, thoughtful)") @RequestParam String mood,
            @Parameter(description = "추천 개수") @RequestParam(defaultValue = "10") Integer limit) {
        
        try {
            List<RecommendationDto> recommendations = recommendationService.getMoodBasedRecommendations(mood, limit);
            return ResponseEntity.ok(recommendations);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/user/{userId}/profile")
    @Operation(summary = "사용자 취향 프로필", description = "사용자의 취향 분석 결과를 조회합니다.")
    public ResponseEntity<RecommendationDto.UserProfileResponse> getUserProfile(@PathVariable UUID userId) {
        try {
            RecommendationDto.UserProfileResponse profile = recommendationService.getUserProfile(userId);
            return ResponseEntity.ok(profile);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/my/profile")
    @Operation(summary = "내 취향 프로필", description = "현재 사용자의 취향 분석 결과를 조회합니다.")
    public ResponseEntity<RecommendationDto.UserProfileResponse> getMyProfile(Authentication authentication) {
        try {
            UUID userId = UUID.fromString(authentication.getName());
            RecommendationDto.UserProfileResponse profile = recommendationService.getUserProfile(userId);
            return ResponseEntity.ok(profile);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/my/social-based")
    @Operation(summary = "내 소셜 기반 추천", description = "현재 사용자에게 소셜 기반 추천을 제공합니다.")
    public ResponseEntity<List<RecommendationDto>> getMySocialBasedRecommendations(
            @Parameter(description = "추천 개수") @RequestParam(defaultValue = "10") Integer limit,
            Authentication authentication) {
        
        try {
            UUID userId = UUID.fromString(authentication.getName());
            List<RecommendationDto> recommendations = recommendationService.getSocialBasedRecommendations(userId, limit);
            return ResponseEntity.ok(recommendations);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/my/mood")
    @Operation(summary = "내 감정 기반 추천", description = "현재 사용자의 기분에 맞는 영화를 추천합니다.")
    public ResponseEntity<List<RecommendationDto>> getMyMoodBasedRecommendations(
            @Parameter(description = "기분") @RequestParam String mood,
            @Parameter(description = "추천 개수") @RequestParam(defaultValue = "10") Integer limit,
            Authentication authentication) {
        
        try {
            List<RecommendationDto> recommendations = recommendationService.getMoodBasedRecommendations(mood, limit);
            return ResponseEntity.ok(recommendations);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
