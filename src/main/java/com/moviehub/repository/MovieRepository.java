package com.moviehub.repository;

import com.moviehub.entity.Movie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MovieRepository extends JpaRepository<Movie, UUID> {
    
    Optional<Movie> findByTmdbId(Integer tmdbId);
    
    @Query("SELECT m FROM Movie m WHERE LOWER(m.title) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "OR LOWER(m.originalTitle) LIKE LOWER(CONCAT('%', :query, '%'))")
    Page<Movie> findByTitleContainingIgnoreCase(@Param("query") String query, Pageable pageable);
    
    @Query("SELECT m FROM Movie m WHERE m.releaseDate BETWEEN :startDate AND :endDate ORDER BY m.popularity DESC")
    List<Movie> findTrendingMovies(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    @Query("SELECT m FROM Movie m WHERE m.voteAverage >= :minRating ORDER BY m.voteAverage DESC")
    Page<Movie> findTopRatedMovies(@Param("minRating") Double minRating, Pageable pageable);
    
    @Query("SELECT m FROM Movie m WHERE m.genres LIKE %:genre% ORDER BY m.popularity DESC")
    Page<Movie> findByGenre(@Param("genre") String genre, Pageable pageable);
    
    @Query("SELECT m FROM Movie m WHERE m.releaseDate >= :fromDate ORDER BY m.releaseDate DESC")
    Page<Movie> findRecentMovies(@Param("fromDate") LocalDate fromDate, Pageable pageable);
    
    @Query("SELECT m FROM Movie m JOIN m.favorites f WHERE f.user.id = :userId ORDER BY f.createdAt DESC")
    List<Movie> findFavoriteMoviesByUser(@Param("userId") UUID userId);
    
    @Query("SELECT m FROM Movie m JOIN m.reviews r WHERE r.user.id = :userId ORDER BY r.createdAt DESC")
    List<Movie> findReviewedMoviesByUser(@Param("userId") UUID userId);
    
    @Query("SELECT m FROM Movie m WHERE m.adult = false ORDER BY m.popularity DESC")
    Page<Movie> findPopularFamilyFriendlyMovies(Pageable pageable);
    
    @Query("SELECT m FROM Movie m WHERE m.runtime BETWEEN :minRuntime AND :maxRuntime ORDER BY m.voteAverage DESC")
    Page<Movie> findByRuntimeRange(@Param("minRuntime") Integer minRuntime, 
                                   @Param("maxRuntime") Integer maxRuntime, 
                                   Pageable pageable);
}
