package com.moviehub.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "movies")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Movie {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(name = "tmdb_id", unique = true)
    private Integer tmdbId;
    
    @Column(nullable = false, length = 500)
    private String title;
    
    @Column(name = "original_title", length = 500)
    private String originalTitle;
    
    @Column(columnDefinition = "TEXT")
    private String overview;
    
    @Column(name = "release_date")
    private LocalDate releaseDate;
    
    @Column(name = "poster_url", length = 500)
    private String posterUrl;
    
    @Column(name = "backdrop_url", length = 500)
    private String backdropUrl;
    
    @Column(name = "vote_average", precision = 3, scale = 1)
    private BigDecimal voteAverage;
    
    @Column(name = "vote_count")
    private Integer voteCount;
    
    @Column(precision = 10, scale = 2)
    private BigDecimal popularity;
    
    @Column(columnDefinition = "TEXT")
    private String genres; // JSON string for genres
    
    private Integer runtime;
    
    @Builder.Default
    private Boolean adult = false;
    
    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Favorite> favorites = new ArrayList<>();
    
    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Review> reviews = new ArrayList<>();
    
    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<CoReview> coReviews = new ArrayList<>();
}
