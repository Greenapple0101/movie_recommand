package com.moviehub.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SimpleController {
    
    @GetMapping("/")
    public String home() {
        return "MovieHub API is running! 🎬";
    }
    
    @GetMapping("/hello")
    public String hello() {
        return "Hello from MovieHub! 🚀";
    }
}

