package com.moviehub.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/test")
public class TestController {
    
    @GetMapping("/hello")
    public Map<String, String> hello() {
        return Map.of("message", "Hello from MovieHub API!", "status", "success");
    }
    
    @GetMapping("/movies")
    public Map<String, Object> testMovies() {
        return Map.of(
            "message", "Movies endpoint is working!",
            "status", "success",
            "data", "Movie data will be here"
        );
    }
}

