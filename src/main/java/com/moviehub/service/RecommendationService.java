package com.moviehub.service;

import com.moviehub.dto.MovieDto;
import com.moviehub.dto.RecommendationDto;
import com.moviehub.entity.Movie;
import com.moviehub.entity.Review;
import com.moviehub.entity.User;
import com.moviehub.entity.Favorite;
import com.moviehub.repository.MovieRepository;
import com.moviehub.repository.ReviewRepository;
import com.moviehub.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class RecommendationService {
    
    private final MovieRepository movieRepository;
    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final MovieService movieService;
    
    @Cacheable(value = "contentBasedRecommendations", key = "#movieId + '_' + #limit")
    public List<RecommendationDto> getContentBasedRecommendations(UUID movieId, Integer limit) {
        Optional<Movie> targetMovie = movieRepository.findById(movieId);
        if (targetMovie.isEmpty()) {
            return List.of();
        }
        
        Movie movie = targetMovie.get();
        List<String> targetGenres = convertStringToGenres(movie.getGenres());
        
        if (targetGenres.isEmpty()) {
            return List.of();
        }
        
        // Find movies with similar genres
        List<Movie> similarMovies = movieRepository.findAll().stream()
                .filter(m -> !m.getId().equals(movieId))
                .filter(m -> {
                    List<String> genres = convertStringToGenres(m.getGenres());
                    return genres.stream().anyMatch(targetGenres::contains);
                })
                .sorted((m1, m2) -> {
                    // Sort by genre similarity and popularity
                    double similarity1 = calculateGenreSimilarity(targetGenres, convertStringToGenres(m1.getGenres()));
                    double similarity2 = calculateGenreSimilarity(targetGenres, convertStringToGenres(m2.getGenres()));
                    
                    if (Double.compare(similarity1, similarity2) != 0) {
                        return Double.compare(similarity2, similarity1);
                    }
                    
                    // If similarity is equal, sort by popularity
                    return m2.getPopularity().compareTo(m1.getPopularity());
                })
                .limit(limit)
                .collect(Collectors.toList());
        
        return similarMovies.stream()
                .map(m -> createRecommendationDto(m, "content_based", 
                        "장르 유사성: " + String.join(", ", targetGenres)))
                .collect(Collectors.toList());
    }
    
    @Cacheable(value = "socialBasedRecommendations", key = "#userId + '_' + #limit")
    public List<RecommendationDto> getSocialBasedRecommendations(UUID userId, Integer limit) {
        // Get user's high-rated movies
        List<Review> userReviews = reviewRepository.findUserHighRatedReviews(userId, 7);
        
        if (userReviews.isEmpty()) {
            return getPopularMoviesRecommendations(limit);
        }
        
        // Get users who liked similar movies
        Set<UUID> similarUserIds = new HashSet<>();
        for (Review review : userReviews) {
            List<User> usersWhoLiked = userRepository.findUsersWhoReviewedMovie(review.getMovie().getId());
            similarUserIds.addAll(usersWhoLiked.stream()
                    .map(User::getId)
                    .filter(id -> !id.equals(userId))
                    .collect(Collectors.toSet()));
        }
        
        // Get movies liked by similar users that current user hasn't reviewed
        Set<UUID> userReviewedMovieIds = userReviews.stream()
                .map(review -> review.getMovie().getId())
                .collect(Collectors.toSet());
        
        Map<UUID, Integer> movieScores = new HashMap<>();
        for (UUID similarUserId : similarUserIds) {
            List<Review> similarUserReviews = reviewRepository.findUserHighRatedReviews(similarUserId, 7);
            for (Review review : similarUserReviews) {
                if (!userReviewedMovieIds.contains(review.getMovie().getId())) {
                    movieScores.merge(review.getMovie().getId(), 1, Integer::sum);
                }
            }
        }
        
        // Sort by score and return top recommendations
        return movieScores.entrySet().stream()
                .sorted(Map.Entry.<UUID, Integer>comparingByValue().reversed())
                .limit(limit)
                .map(entry -> {
                    Movie movie = movieRepository.findById(entry.getKey()).orElse(null);
                    if (movie != null) {
                        return createRecommendationDto(movie, "social_based", 
                                "비슷한 취향 사용자들이 좋아한 영화");
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
    
    @Cacheable(value = "trendingRecommendations", key = "#timeRange + '_' + #limit")
    public List<RecommendationDto> getTrendingRecommendations(String timeRange, Integer limit) {
        // This would typically integrate with external trend data
        // For now, we'll use popular movies as trending
        List<Movie> trendingMovies = movieRepository.findAll().stream()
                .sorted((m1, m2) -> m2.getPopularity().compareTo(m1.getPopularity()))
                .limit(limit)
                .collect(Collectors.toList());
        
        return trendingMovies.stream()
                .map(movie -> createRecommendationDto(movie, "trending", 
                        "현재 인기 있는 영화"))
                .collect(Collectors.toList());
    }
    
    public List<RecommendationDto> getMoodBasedRecommendations(String mood, Integer limit) {
        Map<String, List<String>> moodToGenres = getMoodToGenresMapping();
        List<String> targetGenres = moodToGenres.getOrDefault(mood.toLowerCase(), List.of());
        
        if (targetGenres.isEmpty()) {
            return getPopularMoviesRecommendations(limit);
        }
        
        List<Movie> moodMovies = movieRepository.findAll().stream()
                .filter(movie -> {
                    List<String> genres = convertStringToGenres(movie.getGenres());
                    return genres.stream().anyMatch(targetGenres::contains);
                })
                .sorted((m1, m2) -> {
                    // Sort by mood relevance and popularity
                    double relevance1 = calculateMoodRelevance(targetGenres, convertStringToGenres(m1.getGenres()));
                    double relevance2 = calculateMoodRelevance(targetGenres, convertStringToGenres(m2.getGenres()));
                    
                    if (Double.compare(relevance1, relevance2) != 0) {
                        return Double.compare(relevance2, relevance1);
                    }
                    
                    return m2.getPopularity().compareTo(m1.getPopularity());
                })
                .limit(limit)
                .collect(Collectors.toList());
        
        return moodMovies.stream()
                .map(movie -> createRecommendationDto(movie, "mood_based", 
                        mood + " 기분에 맞는 영화"))
                .collect(Collectors.toList());
    }
    
    public RecommendationDto.UserProfileResponse getUserProfile(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        
        List<Review> userReviews = reviewRepository.findByUserId(userId);
        List<Favorite> userFavorites = user.getFavorites();
        
        // Calculate genre preferences
        Map<String, Integer> genreCounts = new HashMap<>();
        Map<String, Double> genreRatings = new HashMap<>();
        
        for (Review review : userReviews) {
            List<String> genres = convertStringToGenres(review.getMovie().getGenres());
            for (String genre : genres) {
                genreCounts.merge(genre, 1, Integer::sum);
                genreRatings.merge(genre, review.getRating().doubleValue(), Double::sum);
            }
        }
        
        List<RecommendationDto.GenrePreference> genrePreferences = genreCounts.entrySet().stream()
                .map(entry -> {
                    String genre = entry.getKey();
                    int count = entry.getValue();
                    double totalRating = genreRatings.get(genre);
                    double avgRating = totalRating / count;
                    
                    return RecommendationDto.GenrePreference.builder()
                            .genre(genre)
                            .preferenceScore(avgRating / 10.0) // Normalize to 0-1
                            .movieCount(count)
                            .build();
                })
                .sorted((g1, g2) -> Double.compare(g2.getPreferenceScore(), g1.getPreferenceScore()))
                .collect(Collectors.toList());
        
        // Calculate rating pattern
        double averageRating = userReviews.stream()
                .mapToInt(Review::getRating)
                .average()
                .orElse(0.0);
        
        Map<Integer, Long> ratingDistribution = userReviews.stream()
                .collect(Collectors.groupingBy(Review::getRating, Collectors.counting()));
        
        List<RecommendationDto.RatingDistribution> distribution = ratingDistribution.entrySet().stream()
                .map(entry -> {
                    int rating = entry.getKey();
                    long count = entry.getValue();
                    double percentage = (double) count / userReviews.size() * 100;
                    
                    return RecommendationDto.RatingDistribution.builder()
                            .rating(rating)
                            .count((int) count)
                            .percentage(percentage)
                            .build();
                })
                .sorted(Comparator.comparing(RecommendationDto.RatingDistribution::getRating))
                .collect(Collectors.toList());
        
        RecommendationDto.RatingPattern ratingPattern = RecommendationDto.RatingPattern.builder()
                .averageRating(averageRating)
                .totalRatings(userReviews.size())
                .distribution(distribution)
                .build();
        
        return RecommendationDto.UserProfileResponse.builder()
                .userId(userId)
                .username(user.getUsername())
                .genrePreferences(genrePreferences)
                .ratingPattern(ratingPattern)
                .averageRating(averageRating)
                .totalReviews(userReviews.size())
                .totalFavorites(userFavorites.size())
                .build();
    }
    
    private List<RecommendationDto> getPopularMoviesRecommendations(Integer limit) {
        List<Movie> popularMovies = movieRepository.findAll().stream()
                .sorted((m1, m2) -> m2.getPopularity().compareTo(m1.getPopularity()))
                .limit(limit)
                .collect(Collectors.toList());
        
        return popularMovies.stream()
                .map(movie -> createRecommendationDto(movie, "popular", "인기 영화"))
                .collect(Collectors.toList());
    }
    
    private RecommendationDto createRecommendationDto(Movie movie, String type, String reason) {
        return RecommendationDto.builder()
                .movieId(movie.getId())
                .title(movie.getTitle())
                .originalTitle(movie.getOriginalTitle())
                .overview(movie.getOverview())
                .releaseDate(movie.getReleaseDate())
                .posterUrl(movie.getPosterUrl())
                .voteAverage(movie.getVoteAverage())
                .voteCount(movie.getVoteCount())
                .genres(convertStringToGenres(movie.getGenres()))
                .runtime(movie.getRuntime())
                .recommendationReason(reason)
                .confidenceScore(0.8) // Placeholder confidence score
                .recommendationType(type)
                .build();
    }
    
    private double calculateGenreSimilarity(List<String> genres1, List<String> genres2) {
        if (genres1.isEmpty() || genres2.isEmpty()) {
            return 0.0;
        }
        
        Set<String> set1 = new HashSet<>(genres1);
        Set<String> set2 = new HashSet<>(genres2);
        
        Set<String> intersection = new HashSet<>(set1);
        intersection.retainAll(set2);
        
        Set<String> union = new HashSet<>(set1);
        union.addAll(set2);
        
        return (double) intersection.size() / union.size();
    }
    
    private double calculateMoodRelevance(List<String> targetGenres, List<String> movieGenres) {
        if (targetGenres.isEmpty() || movieGenres.isEmpty()) {
            return 0.0;
        }
        
        Set<String> targetSet = new HashSet<>(targetGenres);
        Set<String> movieSet = new HashSet<>(movieGenres);
        
        Set<String> intersection = new HashSet<>(targetSet);
        intersection.retainAll(movieSet);
        
        return (double) intersection.size() / targetGenres.size();
    }
    
    private Map<String, List<String>> getMoodToGenresMapping() {
        Map<String, List<String>> mapping = new HashMap<>();
        mapping.put("happy", List.of("Comedy", "Animation", "Family", "Music"));
        mapping.put("sad", List.of("Drama", "Romance", "Music"));
        mapping.put("excited", List.of("Action", "Adventure", "Thriller", "Science Fiction"));
        mapping.put("relaxed", List.of("Drama", "Romance", "Documentary", "Music"));
        mapping.put("romantic", List.of("Romance", "Drama", "Comedy"));
        mapping.put("scared", List.of("Horror", "Thriller", "Mystery"));
        mapping.put("adventurous", List.of("Adventure", "Action", "Fantasy", "Science Fiction"));
        mapping.put("thoughtful", List.of("Drama", "Documentary", "History", "Biography"));
        return mapping;
    }
    
    private List<String> convertStringToGenres(String genresJson) {
        if (genresJson == null || genresJson.equals("[]")) {
            return List.of();
        }
        String cleanJson = genresJson.replaceAll("[\\[\\]\"]", "");
        if (cleanJson.isEmpty()) {
            return List.of();
        }
        return List.of(cleanJson.split(","));
    }
}
