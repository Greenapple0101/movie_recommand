package com.moviehub.service;

import com.moviehub.dto.ReviewDto;
import com.moviehub.entity.Review;
import com.moviehub.entity.User;
import com.moviehub.entity.Movie;
import com.moviehub.repository.ReviewRepository;
import com.moviehub.repository.UserRepository;
import com.moviehub.repository.MovieRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ReviewService {
    
    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final MovieRepository movieRepository;
    private final SentimentAnalysisService sentimentAnalysisService;
    
    public List<ReviewDto> getReviewsByMovieId(UUID movieId) {
        return reviewRepository.findByMovieId(movieId)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    public List<ReviewDto> getReviewsByUserId(UUID userId) {
        return reviewRepository.findByUserId(userId)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    public Page<ReviewDto> getReviewsByMovieIdPaged(UUID movieId, Pageable pageable) {
        return reviewRepository.findByMovieIdOrderByCreatedAtDesc(movieId, pageable)
                .map(this::convertToDto);
    }
    
    public Page<ReviewDto> getReviewsByUserIdPaged(UUID userId, Pageable pageable) {
        return reviewRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable)
                .map(this::convertToDto);
    }
    
    public Optional<ReviewDto> getReviewByUserAndMovie(UUID userId, UUID movieId) {
        return reviewRepository.findByUserIdAndMovieId(userId, movieId)
                .map(this::convertToDto);
    }
    
    public Optional<Double> getAverageRatingByMovieId(UUID movieId) {
        return reviewRepository.findAverageRatingByMovieId(movieId)
                .map(BigDecimal::doubleValue);
    }
    
    public Long getReviewCountByMovieId(UUID movieId) {
        return reviewRepository.countByMovieId(movieId);
    }
    
    public Page<ReviewDto> getHighRatedReviews(Integer minRating, Pageable pageable) {
        return reviewRepository.findHighRatedReviews(minRating, pageable)
                .map(this::convertToDto);
    }
    
    public Page<ReviewDto> getReviewsBySentiment(Review.SentimentType sentiment, Pageable pageable) {
        return reviewRepository.findBySentiment(sentiment, pageable)
                .map(this::convertToDto);
    }
    
    public List<ReviewDto> getReviewsByMovieAndSentiment(UUID movieId, Review.SentimentType sentiment) {
        return reviewRepository.findByMovieIdAndSentiment(movieId, sentiment)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    public Page<ReviewDto> getReviewsWithComments(Pageable pageable) {
        return reviewRepository.findReviewsWithComments(pageable)
                .map(this::convertToDto);
    }
    
    public List<ReviewDto> getUserHighRatedReviews(UUID userId, Integer minRating) {
        return reviewRepository.findUserHighRatedReviews(userId, minRating)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    @Transactional
    public ReviewDto createReview(UUID userId, ReviewDto.CreateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        
        Movie movie = movieRepository.findById(request.getMovieId())
                .orElseThrow(() -> new IllegalArgumentException("영화를 찾을 수 없습니다."));
        
        // Check if user already reviewed this movie
        if (reviewRepository.existsByUserIdAndMovieId(userId, request.getMovieId())) {
            throw new IllegalArgumentException("이미 이 영화에 대한 리뷰를 작성했습니다.");
        }
        
        // Analyze sentiment if comment is provided
        Review.SentimentType sentiment = Review.SentimentType.NEUTRAL;
        BigDecimal sentimentScore = BigDecimal.ZERO;
        
        if (request.getComment() != null && !request.getComment().trim().isEmpty()) {
            try {
                sentiment = sentimentAnalysisService.analyzeSentiment(request.getComment());
                sentimentScore = sentimentAnalysisService.getSentimentScore(request.getComment());
            } catch (Exception e) {
                log.warn("Failed to analyze sentiment for review: {}", e.getMessage());
            }
        }
        
        Review review = Review.builder()
                .user(user)
                .movie(movie)
                .rating(request.getRating())
                .comment(request.getComment())
                .sentiment(sentiment)
                .sentimentScore(sentimentScore)
                .build();
        
        Review savedReview = reviewRepository.save(review);
        return convertToDto(savedReview);
    }
    
    @Transactional
    public Optional<ReviewDto> updateReview(UUID userId, UUID reviewId, ReviewDto.UpdateRequest request) {
        return reviewRepository.findById(reviewId)
                .filter(review -> review.getUser().getId().equals(userId))
                .map(review -> {
                    review.setRating(request.getRating());
                    review.setComment(request.getComment());
                    
                    // Re-analyze sentiment if comment is provided
                    if (request.getComment() != null && !request.getComment().trim().isEmpty()) {
                        try {
                            review.setSentiment(sentimentAnalysisService.analyzeSentiment(request.getComment()));
                            review.setSentimentScore(sentimentAnalysisService.getSentimentScore(request.getComment()));
                        } catch (Exception e) {
                            log.warn("Failed to analyze sentiment for review update: {}", e.getMessage());
                        }
                    }
                    
                    return convertToDto(reviewRepository.save(review));
                });
    }
    
    @Transactional
    public boolean deleteReview(UUID userId, UUID reviewId) {
        return reviewRepository.findById(reviewId)
                .filter(review -> review.getUser().getId().equals(userId))
                .map(review -> {
                    reviewRepository.delete(review);
                    return true;
                })
                .orElse(false);
    }
    
    public ReviewDto.MovieReviewSummary getMovieReviewSummary(UUID movieId) {
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new IllegalArgumentException("영화를 찾을 수 없습니다."));
        
        Double averageRating = getAverageRatingByMovieId(movieId).orElse(0.0);
        Long totalReviews = getReviewCountByMovieId(movieId);
        
        Long positiveReviews = reviewRepository.countByMovieIdAndSentiment(movieId, Review.SentimentType.POSITIVE);
        Long negativeReviews = reviewRepository.countByMovieIdAndSentiment(movieId, Review.SentimentType.NEGATIVE);
        Long neutralReviews = reviewRepository.countByMovieIdAndSentiment(movieId, Review.SentimentType.NEUTRAL);
        
        return ReviewDto.MovieReviewSummary.builder()
                .movieId(movieId)
                .movieTitle(movie.getTitle())
                .averageRating(averageRating)
                .totalReviews(totalReviews)
                .positiveReviews(positiveReviews)
                .negativeReviews(negativeReviews)
                .neutralReviews(neutralReviews)
                .build();
    }
    
    private ReviewDto convertToDto(Review review) {
        return ReviewDto.builder()
                .id(review.getId())
                .userId(review.getUser().getId())
                .movieId(review.getMovie().getId())
                .username(review.getUser().getUsername())
                .movieTitle(review.getMovie().getTitle())
                .rating(review.getRating())
                .comment(review.getComment())
                .sentiment(review.getSentiment())
                .sentimentScore(review.getSentimentScore())
                .createdAt(review.getCreatedAt())
                .updatedAt(review.getUpdatedAt())
                .build();
    }
}
