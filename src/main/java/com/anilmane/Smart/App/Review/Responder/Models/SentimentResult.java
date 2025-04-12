package com.anilmane.Smart.App.Review.Responder.Models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SentimentResult {
    private double score; // Negative (-1) to Positive (1)
    private String sentiment; // "POSITIVE", "NEGATIVE", "NEUTRAL"
    private List<String> keyIssues; // Extracted key issues or feature requests
    private List<String> mentionedFeatures; // App features mentioned in the review
}
