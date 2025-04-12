package com.anilmane.Smart.App.Review.Responder.Models;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder(toBuilder = true)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AppReview {
    private String id;
    private String userName;
    private String reviewText;
    private int rating;
    private String title;
    private String review_id;
}
