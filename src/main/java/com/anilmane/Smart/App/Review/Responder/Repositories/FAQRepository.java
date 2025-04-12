package com.anilmane.Smart.App.Review.Responder.Repositories;

import com.anilmane.Smart.App.Review.Responder.Models.FAQ;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FAQRepository extends MongoRepository<FAQ, String>{
    List<FAQ> findByCategory(String category);

    @Aggregation(pipeline = {
            "{ $vectorSearch: {",
            "  index: 'faq_index',",
            "  path: 'embedding',",
            "  queryVector: ?0,",
            "  numCandidates: 100,",
            "  limit: ?1",
            "} }"
    })
    List<FAQ> findNearestFAQs(List<Double> queryEmbedding, int limit);


}
