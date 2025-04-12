package com.anilmane.Smart.App.Review.Responder.Services;

import com.anilmane.Smart.App.Review.Responder.Models.AppReview;
import com.anilmane.Smart.App.Review.Responder.Models.SentimentResult;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReviewAnalysisService {

    private final OpenAIService openAIService;

    @Value("${app.reviews.filepath}")
    private Resource reviewsFile;

    public List<AppReview> loadReviewsFromCsv() {
        List<AppReview> reviews = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(reviewsFile.getInputStream(), StandardCharsets.UTF_8));
             CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT
                     .withFirstRecordAsHeader()
                     .withIgnoreHeaderCase()
                     .withTrim())) {

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

            for (CSVRecord record : csvParser) {
                AppReview review = AppReview.builder()
                        .id(record.get("id"))
                        .userName(record.get("user_name"))
                        .reviewText(record.get("review_text"))
                        .rating(Integer.parseInt(record.get("rating")))
                        .build();

                reviews.add(review);
            }

        } catch (Exception e) {
            throw new RuntimeException("Failed to load review data", e);
        }

        return reviews;
    }

//    public SentimentResult analyzeReview(AppReview review) {
//        return openAIService.analyzeSentiment(review);
//    }
}
