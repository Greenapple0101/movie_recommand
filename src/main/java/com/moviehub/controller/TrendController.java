package com.moviehub.controller;

import com.moviehub.dto.TrendDto;
import com.moviehub.service.TrendService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/trends")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Trend", description = "트렌드 관련 API")
public class TrendController {
    
    private final TrendService trendService;
    
    @GetMapping("/recent")
    @Operation(summary = "최근 트렌드 조회", description = "최근 N시간 동안의 트렌드를 조회합니다.")
    public ResponseEntity<List<TrendDto>> getRecentTrends(
            @Parameter(description = "시간 범위 (시간)") @RequestParam(defaultValue = "24") int hours) {
        
        List<TrendDto> trends = trendService.getRecentTrends(hours);
        return ResponseEntity.ok(trends);
    }
    
    @GetMapping("/source/{source}")
    @Operation(summary = "소스별 트렌드 조회", description = "특정 소스의 트렌드를 조회합니다.")
    public ResponseEntity<List<TrendDto>> getTrendsBySource(
            @PathVariable String source,
            @Parameter(description = "시간 범위 (시간)") @RequestParam(defaultValue = "24") int hours) {
        
        List<TrendDto> trends = trendService.getTrendsBySource(source, hours);
        return ResponseEntity.ok(trends);
    }
    
    @GetMapping("/top-keywords")
    @Operation(summary = "상위 트렌딩 키워드", description = "가장 인기 있는 트렌딩 키워드를 조회합니다.")
    public ResponseEntity<List<TrendDto.TrendingKeyword>> getTopTrendingKeywords(
            @Parameter(description = "시간 범위 (시간)") @RequestParam(defaultValue = "24") int hours) {
        
        List<TrendDto.TrendingKeyword> keywords = trendService.getTopTrendingKeywords(hours);
        return ResponseEntity.ok(keywords);
    }
    
    @GetMapping("/search")
    @Operation(summary = "트렌드 검색", description = "키워드로 트렌드를 검색합니다.")
    public ResponseEntity<List<TrendDto>> searchTrends(
            @Parameter(description = "검색 키워드") @RequestParam String keyword) {
        
        List<TrendDto> trends = trendService.searchTrends(keyword);
        return ResponseEntity.ok(trends);
    }
    
    @PostMapping
    @Operation(summary = "트렌드 생성", description = "새로운 트렌드를 생성합니다.")
    public ResponseEntity<TrendDto> createTrend(@RequestBody TrendDto.CreateRequest request) {
        try {
            TrendDto trend = trendService.createTrend(request);
            return ResponseEntity.ok(trend);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "트렌드 삭제", description = "트렌드를 삭제합니다.")
    public ResponseEntity<Void> deleteTrend(@PathVariable UUID id) {
        boolean deleted = trendService.deleteTrend(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}
