package com.moviehub.service;

import com.moviehub.dto.TrendDto;
import com.moviehub.entity.Trend;
import com.moviehub.repository.TrendRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class TrendService {
    
    private final TrendRepository trendRepository;
    private final TmdbService tmdbService;
    
    @Cacheable(value = "recentTrends", key = "#hours")
    public List<TrendDto> getRecentTrends(int hours) {
        LocalDateTime since = LocalDateTime.now().minusHours(hours);
        return trendRepository.findRecentTrends(since)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    @Cacheable(value = "trendsBySource", key = "#source + '_' + #hours")
    public List<TrendDto> getTrendsBySource(String source, int hours) {
        LocalDateTime since = LocalDateTime.now().minusHours(hours);
        return trendRepository.findRecentTrendsBySource(source, since)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    @Cacheable(value = "topTrendingKeywords", key = "#hours")
    public List<TrendDto.TrendingKeyword> getTopTrendingKeywords(int hours) {
        LocalDateTime since = LocalDateTime.now().minusHours(hours);
        List<Object[]> results = trendRepository.findTopTrendingKeywords(since);
        
        return results.stream()
                .map(result -> {
                    String keyword = (String) result[0];
                    Double avgScore = ((Number) result[1]).doubleValue();
                    
                    return TrendDto.TrendingKeyword.builder()
                            .keyword(keyword)
                            .averageScore(avgScore)
                            .build();
                })
                .collect(Collectors.toList());
    }
    
    public List<TrendDto> searchTrends(String keyword) {
        return trendRepository.findByKeywordContaining(keyword)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    @Transactional
    @Async
    @Scheduled(fixedRate = 3600000) // 1시간마다 실행
    public void updateTrendingMovies() {
        log.info("Starting trending movies update...");
        
        try {
            // TMDb에서 트렌딩 영화 데이터 가져오기
            var trendingResponse = tmdbService.getTrendingMovies("day", 1);
            
            if (trendingResponse != null && trendingResponse.getResults() != null) {
                // 상위 10개 영화를 트렌드로 저장
                trendingResponse.getResults().stream()
                        .limit(10)
                        .forEach(movie -> {
                            Trend trend = Trend.builder()
                                    .keyword(movie.getTitle())
                                    .source("TMDb")
                                    .score(movie.getPopularity())
                                    .metadata("{\"movie_id\":" + movie.getId() + ",\"poster_path\":\"" + movie.getPosterPath() + "\"}")
                                    .build();
                            
                            trendRepository.save(trend);
                        });
                
                log.info("Updated trending movies from TMDb");
            }
        } catch (Exception e) {
            log.error("Error updating trending movies: {}", e.getMessage());
        }
    }
    
    @Transactional
    @Async
    @Scheduled(fixedRate = 7200000) // 2시간마다 실행
    public void updatePopularGenres() {
        log.info("Starting popular genres update...");
        
        try {
            // TMDb에서 인기 영화 데이터 가져오기
            var popularResponse = tmdbService.getPopularMovies(1);
            
            if (popularResponse != null && popularResponse.getResults() != null) {
                // 장르별 인기도 계산
                var genreCounts = popularResponse.getResults().stream()
                        .flatMap(movie -> movie.getGenres().stream())
                        .collect(Collectors.groupingBy(
                                genre -> genre.getName(),
                                Collectors.counting()
                        ));
                
                // 상위 5개 장르를 트렌드로 저장
                genreCounts.entrySet().stream()
                        .sorted((e1, e2) -> Long.compare(e2.getValue(), e1.getValue()))
                        .limit(5)
                        .forEach(entry -> {
                            Trend trend = Trend.builder()
                                    .keyword(entry.getKey())
                                    .source("TMDb_Genres")
                                    .score(BigDecimal.valueOf(entry.getValue()))
                                    .metadata("{\"type\":\"genre\"}")
                                    .build();
                            
                            trendRepository.save(trend);
                        });
                
                log.info("Updated popular genres from TMDb");
            }
        } catch (Exception e) {
            log.error("Error updating popular genres: {}", e.getMessage());
        }
    }
    
    @Transactional
    public TrendDto createTrend(TrendDto.CreateRequest request) {
        Trend trend = Trend.builder()
                .keyword(request.getKeyword())
                .source(request.getSource())
                .score(request.getScore())
                .metadata(request.getMetadata())
                .build();
        
        Trend savedTrend = trendRepository.save(trend);
        return convertToDto(savedTrend);
    }
    
    @Transactional
    public boolean deleteTrend(UUID id) {
        if (trendRepository.existsById(id)) {
            trendRepository.deleteById(id);
            return true;
        }
        return false;
    }
    
    private TrendDto convertToDto(Trend trend) {
        return TrendDto.builder()
                .id(trend.getId())
                .keyword(trend.getKeyword())
                .source(trend.getSource())
                .score(trend.getScore())
                .metadata(trend.getMetadata())
                .createdAt(trend.getCreatedAt())
                .build();
    }
}
