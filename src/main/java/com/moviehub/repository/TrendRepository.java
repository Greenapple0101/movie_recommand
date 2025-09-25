package com.moviehub.repository;

import com.moviehub.entity.Trend;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface TrendRepository extends JpaRepository<Trend, UUID> {
    
    List<Trend> findByKeyword(String keyword);
    
    List<Trend> findBySource(String source);
    
    @Query("SELECT t FROM Trend t WHERE t.createdAt >= :since ORDER BY t.score DESC")
    List<Trend> findRecentTrends(@Param("since") LocalDateTime since);
    
    @Query("SELECT t FROM Trend t WHERE t.source = :source AND t.createdAt >= :since ORDER BY t.score DESC")
    List<Trend> findRecentTrendsBySource(@Param("source") String source, @Param("since") LocalDateTime since);
    
    @Query("SELECT t.keyword, AVG(t.score) as avgScore FROM Trend t " +
           "WHERE t.createdAt >= :since " +
           "GROUP BY t.keyword " +
           "ORDER BY avgScore DESC")
    List<Object[]> findTopTrendingKeywords(@Param("since") LocalDateTime since);
    
    @Query("SELECT t FROM Trend t WHERE t.keyword LIKE %:keyword% ORDER BY t.createdAt DESC")
    List<Trend> findByKeywordContaining(@Param("keyword") String keyword);
}
