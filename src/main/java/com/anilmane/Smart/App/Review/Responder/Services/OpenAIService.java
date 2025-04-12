package com.anilmane.Smart.App.Review.Responder.Services;

import com.anilmane.Smart.App.Review.Responder.DTO.ReviewRequest;
import com.anilmane.Smart.App.Review.Responder.Models.AppReview;
import com.anilmane.Smart.App.Review.Responder.Models.FAQ;
import com.anilmane.Smart.App.Review.Responder.Models.SentimentResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatCompletionResult;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.embedding.EmbeddingRequest;
import com.theokanning.openai.embedding.EmbeddingResult;
import com.theokanning.openai.service.OpenAiService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OpenAIService {

    @Autowired
    private OpenAiService openAiService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${openai.model}")
    private String model;

    @Value("${openai.embedding.model}")
    private String embeddingModel;

    @Value("${openai.max-tokens}")
    private int maxTokens;

    @Value("${openai.temperature}")
    private double temperature;

    public List<Double> createEmbedding(String text) {
        EmbeddingRequest embeddingRequest = EmbeddingRequest.builder()
                .model(embeddingModel)
                .input(List.of(text))
                .build();

        EmbeddingResult embeddings = openAiService.createEmbeddings(embeddingRequest);

        List<Double> embeddingList = embeddings.getData().get(0).getEmbedding();


       return embeddingList;
    }


    public SentimentResult analyzeSentiment(ReviewRequest review) {
        String systemPrompt = "You are an expert at analyzing app reviews. " +
                "Extract sentiment (POSITIVE, NEGATIVE, or NEUTRAL), sentiment score (-1 to 1), " +
                "key issues or feature requests mentioned, and any specific app features mentioned. " +
                "Respond in valid JSON format with these fields: sentiment, score, keyIssues, mentionedFeatures";

        ChatCompletionRequest request = ChatCompletionRequest.builder()
                .model(model)
                .messages(Arrays.asList(
                        new ChatMessage("system", systemPrompt),
                        new ChatMessage("user", "Review: \"" + review.getReviewText() + "\"\nRating: " + review.getRating() + " stars")
                ))
                .maxTokens(maxTokens)
                .temperature(temperature)
                .build();

        ChatCompletionResult result = openAiService.createChatCompletion(request);
        String jsonResponse = result.getChoices().get(0).getMessage().getContent();
        jsonResponse = jsonResponse.replaceAll("```json|```", "").trim();
        System.out.println("Sentiment Analysis JSON Response: " + jsonResponse);
        try {
            return objectMapper.readValue(jsonResponse, SentimentResult.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse sentiment analysis result", e);
        }
    }

    public String generateResponse(ReviewRequest review, SentimentResult sentiment, List<FAQ> relevantFaqs) {
        StringBuilder faqContext = new StringBuilder();
        if (!relevantFaqs.isEmpty()) {
            faqContext.append("Relevant FAQs to reference:\n");
            for (int i = 0; i < relevantFaqs.size(); i++) {
                FAQ faq = relevantFaqs.get(i);
                faqContext.append(i + 1).append(". Q: ").append(faq.getQuestion())
                        .append("\n   A: ").append(faq.getAnswer()).append("\n\n");
            }
        }

//        String systemPrompt = "You are a helpful customer support representative responding to an app review. " +
//                "Be personable and address the specific points in their review. " +
//                "If they have positive feedback, thank them and suggest related features. " +
//                "If they mention issues, be empathetic and provide solutions when possible. " +
//                "Keep your response friendly, concise, and on-brand.";

//        StringBuilder userPrompt = new StringBuilder();
////        userPrompt.append("Please generate a response to this app review:\n\n");
////        userPrompt.append("Review: \"").append(review.getReviewText()).append("\"\n");
////        userPrompt.append("Rating: ").append(review.getRating()).append(" stars (out of 5)\n");
//
//        userPrompt.append("Sentiment analysis: ").append(sentiment.getSentiment()).append("\n");
//        userPrompt.append("Key aspects mentioned: ").append(String.join(", ", sentiment.getAspects())).append("\n\n");

//        if (!relevantFaqs.isEmpty()) {
//            userPrompt.append("Relevant FAQ information to reference if needed:\n");
//            for (FAQ faq : relevantFaqs) {
//                userPrompt.append("- Q: ").append(faq.getQuestion()).append("\n");
//                userPrompt.append("  A: ").append(faq.getAnswer()).append("\n\n");
//            }
//        }

//        userPrompt.append("Guidelines:\n");
////        userPrompt.append("1. Address the user by name\n");
////        userPrompt.append("2. Keep response under 150 words\n");
//        userPrompt.append("3. Be specific to their feedback\n");
//        userPrompt.append("4. Always sign with 'The [App Name] Team'\n");


        String systemPrompt;
        if (sentiment.getSentiment().equalsIgnoreCase("NEGATIVE")) {
            systemPrompt = "You are a helpful customer support representative responding to a positive app review. " +
                    "Thank the user for their positive feedback, acknowledge specific things they mentioned liking, " +
                    "and suggest 1-2 other features they might enjoy based on their review. " +
                    "Keep your response friendly, concise, and on-brand. Don't be overly enthusiastic.";
        } else {
            systemPrompt = "You are a helpful customer support representative responding to a negative app review. " +
                    "Be empathetic and understanding about their concerns. Address their specific issues by " +
                    "referencing information from the relevant FAQs. Provide clear steps to resolve their problem " +
                    "when possible. Keep your response sincere, helpful, and concise.";
        }

        System.out.println("FAQ Context: " + faqContext.toString());
        ChatCompletionRequest request = ChatCompletionRequest.builder()
                .model(model)
                .messages(Arrays.asList(
                        new ChatMessage("system", systemPrompt),
                        new ChatMessage("user", "App Review Details:\n" +
                                "Rating: " + review.getRating() + " stars\n" +
                                "Review: \"" + review.getReviewText() + "\"\n" +
                                "Sentiment: " + sentiment.getSentiment() + "\n" +
                                "Key Issues: " + String.join(", ", sentiment.getKeyIssues()) + "\n\n" +
                                "Relevent FAQ:" +faqContext.toString() +
                                "\nWrite a helpful, empathetic response addressing their specific feedback.")
                ))
                .maxTokens(maxTokens)
                .temperature(temperature)
                .build();

        ChatCompletionResult result = openAiService.createChatCompletion(request);
        return result.getChoices().get(0).getMessage().getContent();
    }
}
