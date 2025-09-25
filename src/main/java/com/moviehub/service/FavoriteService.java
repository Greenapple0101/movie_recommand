package com.moviehub.service;

import com.moviehub.dto.FavoriteDto;
import com.moviehub.entity.Favorite;
import com.moviehub.entity.User;
import com.moviehub.entity.Movie;
import com.moviehub.repository.FavoriteRepository;
import com.moviehub.repository.UserRepository;
import com.moviehub.repository.MovieRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class FavoriteService {
    
    private final FavoriteRepository favoriteRepository;
    private final UserRepository userRepository;
    private final MovieRepository movieRepository;
    
    public List<FavoriteDto> getFavoritesByUserId(UUID userId) {
        return favoriteRepository.findByUserId(userId)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    public List<FavoriteDto> getFavoritesByMovieId(UUID movieId) {
        return favoriteRepository.findByMovieId(movieId)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    public List<FavoriteDto> getFavoritesByUserIdOrderByCreatedAt(UUID userId) {
        return favoriteRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    public Optional<FavoriteDto> getFavoriteByUserAndMovie(UUID userId, UUID movieId) {
        return favoriteRepository.findUserFavoriteForMovie(userId, movieId)
                .map(this::convertToDto);
    }
    
    public boolean isFavorite(UUID userId, UUID movieId) {
        return favoriteRepository.existsByUserIdAndMovieId(userId, movieId);
    }
    
    public Long getFavoriteCountByMovieId(UUID movieId) {
        return favoriteRepository.countByMovieId(movieId);
    }
    
    @Transactional
    public FavoriteDto addFavorite(UUID userId, UUID movieId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new IllegalArgumentException("영화를 찾을 수 없습니다."));
        
        // Check if already favorited
        if (favoriteRepository.existsByUserIdAndMovieId(userId, movieId)) {
            throw new IllegalArgumentException("이미 즐겨찾기에 추가된 영화입니다.");
        }
        
        Favorite favorite = Favorite.builder()
                .user(user)
                .movie(movie)
                .build();
        
        Favorite savedFavorite = favoriteRepository.save(favorite);
        return convertToDto(savedFavorite);
    }
    
    @Transactional
    public boolean removeFavorite(UUID userId, UUID movieId) {
        Optional<Favorite> favorite = favoriteRepository.findUserFavoriteForMovie(userId, movieId);
        
        if (favorite.isPresent()) {
            favoriteRepository.delete(favorite.get());
            return true;
        }
        
        return false;
    }
    
    @Transactional
    public boolean toggleFavorite(UUID userId, UUID movieId) {
        if (isFavorite(userId, movieId)) {
            return removeFavorite(userId, movieId);
        } else {
            try {
                addFavorite(userId, movieId);
                return true;
            } catch (IllegalArgumentException e) {
                return false;
            }
        }
    }
    
    private FavoriteDto convertToDto(Favorite favorite) {
        return FavoriteDto.builder()
                .id(favorite.getId())
                .userId(favorite.getUser().getId())
                .movieId(favorite.getMovie().getId())
                .username(favorite.getUser().getUsername())
                .movieTitle(favorite.getMovie().getTitle())
                .moviePosterUrl(favorite.getMovie().getPosterUrl())
                .createdAt(favorite.getCreatedAt())
                .build();
    }
}
