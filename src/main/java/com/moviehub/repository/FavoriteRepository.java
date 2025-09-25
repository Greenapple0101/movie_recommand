package com.moviehub.repository;

import com.moviehub.entity.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, UUID> {
    
    Optional<Favorite> findByUserIdAndMovieId(UUID userId, UUID movieId);
    
    List<Favorite> findByUserId(UUID userId);
    
    List<Favorite> findByMovieId(UUID movieId);
    
    boolean existsByUserIdAndMovieId(UUID userId, UUID movieId);
    
    @Query("SELECT f FROM Favorite f WHERE f.user.id = :userId ORDER BY f.createdAt DESC")
    List<Favorite> findByUserIdOrderByCreatedAtDesc(@Param("userId") UUID userId);
    
    @Query("SELECT COUNT(f) FROM Favorite f WHERE f.movie.id = :movieId")
    Long countByMovieId(@Param("movieId") UUID movieId);
    
    @Query("SELECT f FROM Favorite f WHERE f.user.id = :userId AND f.movie.id = :movieId")
    Optional<Favorite> findUserFavoriteForMovie(@Param("userId") UUID userId, @Param("movieId") UUID movieId);
}
