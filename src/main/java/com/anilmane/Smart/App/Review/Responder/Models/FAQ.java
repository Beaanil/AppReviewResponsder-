package com.anilmane.Smart.App.Review.Responder.Models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;




@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "faqs")
public class FAQ {
    @Id
    private String id;
    private String question;
    private String answer;
    private String category;
    private List<Double> embedding;
}
