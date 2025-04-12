package com.anilmane.Smart.App.Review.Responder.Controller;

import com.anilmane.Smart.App.Review.Responder.DTO.ReviewRequest;
import com.anilmane.Smart.App.Review.Responder.Models.AppReview;
import com.anilmane.Smart.App.Review.Responder.Models.ReviewResponse;
import com.anilmane.Smart.App.Review.Responder.Services.FAQService;
import com.anilmane.Smart.App.Review.Responder.Services.ResponseGenerationService;
import com.anilmane.Smart.App.Review.Responder.Services.ReviewAnalysisService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/reviews")
public class ReviewResponderController {

    @Autowired
    private  ReviewAnalysisService reviewAnalysisService;

    @Autowired
    private  ResponseGenerationService responseGenerationService;

    @Autowired
    private FAQService faqService;
    @PostMapping("/respond")
    public ResponseEntity<ReviewResponse> respondToReview(@RequestBody ReviewRequest review) {
        ReviewResponse response = responseGenerationService.generateResponse(review);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/process")
    public void processFaq(@RequestParam ("file") MultipartFile faqFile) {
        faqService.loadFaqsFromCsv(faqFile);
    }


}
