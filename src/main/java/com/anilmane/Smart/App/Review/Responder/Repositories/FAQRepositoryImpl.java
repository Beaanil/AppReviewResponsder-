package com.anilmane.Smart.App.Review.Responder.Repositories;

import com.anilmane.Smart.App.Review.Responder.Models.FAQ;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FAQRepositoryImpl implements FAQRepositoryCustom {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<FAQ> findNearestFAQs(float[] queryEmbedding, int limit) {
        // Convert float array to properly formatted PostgreSQL vector string
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < queryEmbedding.length; i++) {
            if (i > 0) sb.append(",");
            sb.append(queryEmbedding[i]);
        }
        sb.append("]");

        // Create and execute native query
        Query query = entityManager.createNativeQuery(
                "SELECT * FROM faqs ORDER BY embedding <-> cast(:queryEmbedding AS vector) LIMIT :limit",
                FAQ.class);
        query.setParameter("queryEmbedding", sb);
        query.setParameter("limit", limit);

        return query.getResultList();
    }
}
