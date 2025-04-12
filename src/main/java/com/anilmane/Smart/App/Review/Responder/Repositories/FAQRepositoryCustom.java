package com.anilmane.Smart.App.Review.Responder.Repositories;

import com.anilmane.Smart.App.Review.Responder.Models.FAQ;

import java.util.List;

public interface FAQRepositoryCustom {

    List<FAQ> findNearestFAQs(float[] queryEmbedding, int limit);
}
