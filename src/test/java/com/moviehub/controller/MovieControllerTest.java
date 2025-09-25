package com.moviehub.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.moviehub.entity.Movie;
import com.moviehub.repository.MovieRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("test")
@Transactional
class MovieControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        // Create test movie
        Movie movie = Movie.builder()
                .title("Test Movie")
                .originalTitle("Test Movie Original")
                .overview("A test movie for testing purposes")
                .releaseDate(LocalDate.of(2023, 1, 1))
                .voteAverage(BigDecimal.valueOf(8.5))
                .voteCount(1000)
                .popularity(BigDecimal.valueOf(100.0))
                .genres("[\"Action\", \"Drama\"]")
                .runtime(120)
                .adult(false)
                .build();

        movieRepository.save(movie);
    }

    @Test
    void getAllMovies_ShouldReturnMovies() throws Exception {
        mockMvc.perform(get("/api/movies")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].title").value("Test Movie"));
    }

    @Test
    void searchMovies_ShouldReturnMatchingMovies() throws Exception {
        mockMvc.perform(get("/api/movies/search")
                        .param("query", "Test")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].title").value("Test Movie"));
    }

    @Test
    void getMovieById_WithValidId_ShouldReturnMovie() throws Exception {
        Movie movie = movieRepository.findAll().get(0);
        
        mockMvc.perform(get("/api/movies/{id}", movie.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.title").value("Test Movie"));
    }

    @Test
    void getMovieById_WithInvalidId_ShouldReturnNotFound() throws Exception {
        mockMvc.perform(get("/api/movies/{id}", "00000000-0000-0000-0000-000000000000")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
