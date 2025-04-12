package com.anilmane.Smart.App.Review.Responder.Services;

import com.anilmane.Smart.App.Review.Responder.DTO.ReviewRequest;
import com.anilmane.Smart.App.Review.Responder.Models.AppReview;
import com.anilmane.Smart.App.Review.Responder.Models.FAQ;
import com.anilmane.Smart.App.Review.Responder.Models.ReviewResponse;
import com.anilmane.Smart.App.Review.Responder.Models.SentimentResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ResponseGenerationService {

    private final OpenAIService openAIService;
    private final FAQService faqService;

    public ReviewResponse generateResponse(ReviewRequest review) {
        // Step 1: Analyze review sentiment
       SentimentResult sentimentResult = openAIService.analyzeSentiment(review);

        // Step 2: Find relevant FAQs
        List<FAQ> relevantFaqs = faqService.findRelevantFaqs1(review.getReviewText(), 20);

        // Step 3: Generate response text
        String responseText = openAIService.generateResponse(review, sentimentResult, relevantFaqs);

        // Step 4: Create response object
        return ReviewResponse.builder()
                .responseText(responseText)
                .sentiment(sentimentResult.getSentiment())
                .build();
    }
}