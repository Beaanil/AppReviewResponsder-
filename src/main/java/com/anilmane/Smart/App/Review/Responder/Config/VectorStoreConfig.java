package com.anilmane.Smart.App.Review.Responder.Config;

import com.github.jelmerk.knn.Index;
import com.github.jelmerk.knn.Item;
import com.github.jelmerk.knn.hnsw.HnswIndex;
import lombok.Data;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.github.jelmerk.knn.DistanceFunctions;

import java.io.Serializable;


@Configuration
public class VectorStoreConfig {

    @Value("${vector.store.dimensions}")
    private int dimensions;

    @Value("${vector.store.ef-construction}")
    private int efConstruction;

    @Value("${vector.store.m}")
    private int m;

    @Getter
    public static class FAQItem implements Item<String, float[]>, Serializable {
        private static final long serialVersionUID = 1L;

        private final String id;
        private final float[] vector;

        public FAQItem(String id, float[] vector) {
            this.id = id;
            this.vector = vector;
        }

        @Override
        public String id() {
            return id;
        }

        @Override
        public float[] vector() {
            return vector;
        }

        @Override
        public int dimensions() {
            return vector.length;
        }
    }

    @Bean
    public Index<String, float[], FAQItem, Float> vectorIndex() {
        return HnswIndex
                .newBuilder(dimensions, DistanceFunctions.FLOAT_EUCLIDEAN_DISTANCE, 10000)
                .withM(m)
                .withEfConstruction(efConstruction)
                .build();
    }
}