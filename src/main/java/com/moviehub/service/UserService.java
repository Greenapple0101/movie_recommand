package com.moviehub.service;

import com.moviehub.dto.UserDto;
import com.moviehub.entity.User;
import com.moviehub.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
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
public class UserService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    public List<UserDto> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    public Optional<UserDto> getUserById(UUID id) {
        return userRepository.findById(id)
                .map(this::convertToDto);
    }
    
    public Optional<UserDto> getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(this::convertToDto);
    }
    
    public Optional<UserDto> getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .map(this::convertToDto);
    }
    
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
    
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }
    
    @Transactional
    public UserDto createUser(UserDto.RegisterRequest request) {
        if (existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }
        
        if (existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("이미 존재하는 사용자명입니다.");
        }
        
        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .username(request.getUsername())
                .role(User.UserRole.USER)
                .build();
        
        User savedUser = userRepository.save(user);
        return convertToDto(savedUser);
    }
    
    @Transactional
    public Optional<UserDto> updateUser(UUID id, UserDto.UpdateRequest request) {
        return userRepository.findById(id)
                .map(user -> {
                    if (request.getUsername() != null && !request.getUsername().equals(user.getUsername())) {
                        if (existsByUsername(request.getUsername())) {
                            throw new IllegalArgumentException("이미 존재하는 사용자명입니다.");
                        }
                        user.setUsername(request.getUsername());
                    }
                    
                    if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())) {
                        if (existsByEmail(request.getEmail())) {
                            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
                        }
                        user.setEmail(request.getEmail());
                    }
                    
                    return convertToDto(userRepository.save(user));
                });
    }
    
    @Transactional
    public boolean changePassword(UUID id, UserDto.ChangePasswordRequest request) {
        return userRepository.findById(id)
                .map(user -> {
                    if (passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
                        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
                        userRepository.save(user);
                        return true;
                    }
                    return false;
                })
                .orElse(false);
    }
    
    @Transactional
    public boolean deleteUser(UUID id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return true;
        }
        return false;
    }
    
    public UserDto.ProfileResponse getUserProfile(UUID id) {
        return userRepository.findById(id)
                .map(user -> {
                    // Calculate user statistics
                    long totalReviews = user.getReviews().size();
                    long totalFavorites = user.getFavorites().size();
                    double averageRating = user.getReviews().stream()
                            .mapToInt(review -> review.getRating())
                            .average()
                            .orElse(0.0);
                    
                    return UserDto.ProfileResponse.builder()
                            .id(user.getId())
                            .email(user.getEmail())
                            .username(user.getUsername())
                            .role(user.getRole())
                            .createdAt(user.getCreatedAt())
                            .totalReviews(totalReviews)
                            .totalFavorites(totalFavorites)
                            .averageRating(averageRating)
                            .build();
                })
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
    }
    
    public List<UserDto> getUsersWhoReviewedMovie(UUID movieId) {
        return userRepository.findUsersWhoReviewedMovie(movieId)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    public List<UserDto> getUsersWhoFavoritedMovie(UUID movieId) {
        return userRepository.findUsersWhoFavoritedMovie(movieId)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    private UserDto convertToDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .username(user.getUsername())
                .role(user.getRole())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}
