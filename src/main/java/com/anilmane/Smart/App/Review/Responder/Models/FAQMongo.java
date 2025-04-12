package com.anilmane.Smart.App.Review.Responder.Models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "faqs")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FAQMongo {
    @Id
    private String id;
    private String question;
    private String answer;
    private String category;
    private List<Double> embedding;
}
