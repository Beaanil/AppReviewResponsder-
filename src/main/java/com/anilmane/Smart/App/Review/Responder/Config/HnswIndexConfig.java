package com.anilmane.Smart.App.Review.Responder.Config;

import com.github.jelmerk.knn.DistanceFunction;
import com.github.jelmerk.knn.DistanceFunctions;
import com.github.jelmerk.knn.hnsw.HnswIndex;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.util.Comparator;

@Configuration
public class HnswIndexConfig {

    @Bean(name = "hnswVectorIndex")
    public HnswIndex<String, float[], VectorStoreConfig.FAQItem, Float> vectorIndex() throws IOException {
        int dimensions = 1536;
        int maxNodes = 10000;
        int M = 16;
        int efConstruction = 100;

        DistanceFunction<float[], Float> distanceFunction = DistanceFunctions.FLOAT_COSINE_DISTANCE;
        Comparator<Float> comparator = Comparator.naturalOrder(); // ✅ For sorting distances

        return HnswIndex.<float[], Float>newBuilder(dimensions, distanceFunction, comparator, maxNodes)
                .withM(M)
                .withEf(efConstruction)
                .build();
    }
}
