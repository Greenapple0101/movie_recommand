package com.moviehub.service;

import com.moviehub.entity.Review;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class SentimentAnalysisService {
    
    // Simple sentiment analysis using keyword matching
    // In a real application, you would use more sophisticated NLP libraries
    private final Map<String, Double> positiveWords = new HashMap<>();
    private final Map<String, Double> negativeWords = new HashMap<>();
    
    public SentimentAnalysisService() {
        initializeSentimentWords();
    }
    
    private void initializeSentimentWords() {
        // Positive words with weights
        positiveWords.put("좋다", 0.8);
        positiveWords.put("훌륭하다", 0.9);
        positiveWords.put("최고", 1.0);
        positiveWords.put("멋지다", 0.8);
        positiveWords.put("재미있다", 0.7);
        positiveWords.put("감동적", 0.8);
        positiveWords.put("훌륭한", 0.9);
        positiveWords.put("완벽", 1.0);
        positiveWords.put("사랑", 0.8);
        positiveWords.put("추천", 0.7);
        positiveWords.put("great", 0.8);
        positiveWords.put("excellent", 0.9);
        positiveWords.put("amazing", 0.9);
        positiveWords.put("wonderful", 0.8);
        positiveWords.put("fantastic", 0.9);
        positiveWords.put("awesome", 0.8);
        positiveWords.put("brilliant", 0.9);
        positiveWords.put("perfect", 1.0);
        positiveWords.put("love", 0.8);
        positiveWords.put("recommend", 0.7);
        
        // Negative words with weights
        negativeWords.put("나쁘다", -0.8);
        negativeWords.put("최악", -1.0);
        negativeWords.put("싫다", -0.7);
        negativeWords.put("지루하다", -0.6);
        negativeWords.put("실망", -0.8);
        negativeWords.put("별로", -0.6);
        negativeWords.put("재미없다", -0.7);
        negativeWords.put("끔찍", -0.9);
        negativeWords.put("혐오", -0.9);
        negativeWords.put("싫어", -0.7);
        negativeWords.put("bad", -0.8);
        negativeWords.put("terrible", -0.9);
        negativeWords.put("awful", -0.9);
        negativeWords.put("horrible", -0.9);
        negativeWords.put("boring", -0.6);
        negativeWords.put("disappointing", -0.8);
        negativeWords.put("hate", -0.8);
        negativeWords.put("worst", -1.0);
        negativeWords.put("stupid", -0.7);
        negativeWords.put("waste", -0.8);
    }
    
    public Review.SentimentType analyzeSentiment(String text) {
        if (text == null || text.trim().isEmpty()) {
            return Review.SentimentType.NEUTRAL;
        }
        
        double sentimentScore = calculateSentimentScore(text);
        
        if (sentimentScore > 0.1) {
            return Review.SentimentType.POSITIVE;
        } else if (sentimentScore < -0.1) {
            return Review.SentimentType.NEGATIVE;
        } else {
            return Review.SentimentType.NEUTRAL;
        }
    }
    
    public BigDecimal getSentimentScore(String text) {
        if (text == null || text.trim().isEmpty()) {
            return BigDecimal.ZERO;
        }
        
        double score = calculateSentimentScore(text);
        return BigDecimal.valueOf(Math.max(-1.0, Math.min(1.0, score)));
    }
    
    private double calculateSentimentScore(String text) {
        String lowerText = text.toLowerCase();
        double totalScore = 0.0;
        int wordCount = 0;
        
        // Check for positive words
        for (Map.Entry<String, Double> entry : positiveWords.entrySet()) {
            if (lowerText.contains(entry.getKey())) {
                totalScore += entry.getValue();
                wordCount++;
            }
        }
        
        // Check for negative words
        for (Map.Entry<String, Double> entry : negativeWords.entrySet()) {
            if (lowerText.contains(entry.getKey())) {
                totalScore += entry.getValue();
                wordCount++;
            }
        }
        
        // Normalize score
        if (wordCount > 0) {
            return totalScore / wordCount;
        }
        
        return 0.0;
    }
}
