package com.anilmane.Smart.App.Review.Responder.Services;
import com.anilmane.Smart.App.Review.Responder.Models.FAQ;
import com.anilmane.Smart.App.Review.Responder.Config.VectorStoreConfig.FAQItem;
import com.anilmane.Smart.App.Review.Responder.Repositories.FAQRepository;
import com.anilmane.Smart.App.Review.Responder.Repositories.FAQRepositoryImpl;

import com.github.jelmerk.knn.SearchResult;
import com.github.jelmerk.knn.hnsw.HnswIndex;

import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.Resource;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.List;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;


@Service
public class FAQService {

    private final FAQRepository faqRepository;
    private final OpenAIService openAIService;
    @Autowired
    @Qualifier("hnswVectorIndex") // Match the new bean name
    private final HnswIndex<String, float[], FAQItem, Float> vectorIndex;

    @Autowired
    public FAQService(FAQRepository faqRepository,
                      OpenAIService openAIService,
                      HnswIndex<String, float[], FAQItem, Float> vectorIndex) {
        this.faqRepository = faqRepository;
        this.openAIService = openAIService;
        this.vectorIndex = vectorIndex;
    }
    @Autowired
    private FAQRepositoryImpl faqRepositoryImpl;
    @Value("${app.faq.filepath}")
    private Resource faqFile;

    public void loadFaqsFromCsv(MultipartFile faqFile) {
        try (Reader reader = new BufferedReader(new InputStreamReader(faqFile.getInputStream(), StandardCharsets.UTF_8))) {
            CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim());

            for (CSVRecord record : csvParser) {
                String id = UUID.randomUUID().toString();
                String question = record.get("User Query");
                String answer = record.get("Product Responses");
                String category = record.get("Product");
                String content =  question + "" + answer;
                List<Double> embedding = openAIService.createEmbedding(content);

                FAQ faq = FAQ.builder()
                        .id(id)
                        .question(question)
                        .answer(answer)
                        .category(category)
                        .embedding(embedding)
                        .build();

                faqRepository.save(faq);

//                vectorIndex.add(new FAQItem(id, embedding));
            }

        } catch (Exception e) {
            throw new RuntimeException("Failed to load FAQ data", e);
        }
    }

    public List<FAQ> findRelevantFaqs1(String query, int limit) {
      List<Double> queryEmbedding = openAIService.createEmbedding(query);

        double[] queryVector = queryEmbedding.stream().mapToDouble(Double::doubleValue).toArray();

        String queryVectorString = Arrays.toString(queryVector)
                .replace("[", "{")
                .replace("]", "}");


       List<FAQ> faqs = getReleventFAQs(queryVectorString, limit);
        return faqs;
    }


    public List<FAQ> getReleventFAQs(String queryVector, int limit) {
        var client = MongoClients.create();  // add your mongo atlas url here
        MongoDatabase database = client.getDatabase("smart-review-db");
        MongoCollection<Document> collection = database.getCollection("faqs");

        // Fix: Convert queryVector string to List<Double>
        List<Double> queryVectorList = Arrays.stream(queryVector.replace("{", "").replace("}", "").split(","))
                .map(String::trim)
                .map(Double::parseDouble)  // Convert to double
                .collect(Collectors.toList());

        Document vectorSearchStage = new Document("$vectorSearch",
                new Document("index", "faq_index")
                        .append("path", "embedding")
                        .append("queryVector", queryVectorList)
                        .append("numCandidates", 100)
                        .append("limit", limit)
        );

        AggregateIterable<Document> result = collection.aggregate(Arrays.asList(vectorSearchStage));

        List<FAQ> faqs = new ArrayList<>();
        result.forEach(doc ->
                faqs.add(new FAQ(
                        doc.getString("_id"),  // Use getString() instead of getObjectId()
                        doc.getString("question"),
                        doc.getString("answer"),
                        doc.getString("category"),
                        (List<Double>) doc.get("embedding")
                ))
        );

        return faqs;
    }

}
