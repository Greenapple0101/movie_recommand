package com.moviehub.repository;

import com.moviehub.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ReviewRepository extends JpaRepository<Review, UUID> {
    
    Optional<Review> findByUserIdAndMovieId(UUID userId, UUID movieId);
    
    List<Review> findByMovieId(UUID movieId);
    
    List<Review> findByUserId(UUID userId);
    
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.movie.id = :movieId")
    Optional<BigDecimal> findAverageRatingByMovieId(@Param("movieId") UUID movieId);
    
    @Query("SELECT COUNT(r) FROM Review r WHERE r.movie.id = :movieId")
    Long countByMovieId(@Param("movieId") UUID movieId);
    
    @Query("SELECT r FROM Review r WHERE r.movie.id = :movieId ORDER BY r.createdAt DESC")
    Page<Review> findByMovieIdOrderByCreatedAtDesc(@Param("movieId") UUID movieId, Pageable pageable);
    
    @Query("SELECT r FROM Review r WHERE r.user.id = :userId ORDER BY r.createdAt DESC")
    Page<Review> findByUserIdOrderByCreatedAtDesc(@Param("userId") UUID userId, Pageable pageable);
    
    @Query("SELECT r FROM Review r WHERE r.rating >= :minRating ORDER BY r.createdAt DESC")
    Page<Review> findHighRatedReviews(@Param("minRating") Integer minRating, Pageable pageable);
    
    @Query("SELECT r FROM Review r WHERE r.sentiment = :sentiment ORDER BY r.createdAt DESC")
    Page<Review> findBySentiment(@Param("sentiment") Review.SentimentType sentiment, Pageable pageable);
    
    @Query("SELECT r FROM Review r WHERE r.movie.id = :movieId AND r.sentiment = :sentiment")
    List<Review> findByMovieIdAndSentiment(@Param("movieId") UUID movieId, 
                                           @Param("sentiment") Review.SentimentType sentiment);
    
    @Query("SELECT r FROM Review r WHERE r.comment IS NOT NULL AND LENGTH(r.comment) > 0 ORDER BY r.createdAt DESC")
    Page<Review> findReviewsWithComments(Pageable pageable);
    
    @Query("SELECT r FROM Review r WHERE r.user.id = :userId AND r.rating >= :minRating ORDER BY r.createdAt DESC")
    List<Review> findUserHighRatedReviews(@Param("userId") UUID userId, @Param("minRating") Integer minRating);
    
    @Query("SELECT COUNT(r) FROM Review r WHERE r.movie.id = :movieId AND r.sentiment = :sentiment")
    Long countByMovieIdAndSentiment(@Param("movieId") UUID movieId, @Param("sentiment") Review.SentimentType sentiment);
    
    boolean existsByUserIdAndMovieId(UUID userId, UUID movieId);
}