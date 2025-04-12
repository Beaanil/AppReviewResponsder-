package com.anilmane.Smart.App.Review.Responder.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReviewRequest {
    String reviewText;
    int rating;
}
