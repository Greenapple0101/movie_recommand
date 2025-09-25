package com.moviehub.repository;

import com.moviehub.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    
    Optional<User> findByEmail(String email);
    
    Optional<User> findByUsername(String username);
    
    boolean existsByEmail(String email);
    
    boolean existsByUsername(String username);
    
    @Query("SELECT u FROM User u WHERE u.role = 'USER'")
    List<User> findAllUsers();
    
    @Query("SELECT u FROM User u JOIN u.reviews r WHERE r.movie.id = :movieId")
    List<User> findUsersWhoReviewedMovie(@Param("movieId") UUID movieId);
    
    @Query("SELECT u FROM User u JOIN u.favorites f WHERE f.movie.id = :movieId")
    List<User> findUsersWhoFavoritedMovie(@Param("movieId") UUID movieId);
}
