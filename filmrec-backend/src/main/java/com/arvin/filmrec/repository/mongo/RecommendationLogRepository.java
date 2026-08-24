package com.arvin.filmrec.repository.mongo;

import com.arvin.filmrec.document.RecommendationLog;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface RecommendationLogRepository extends MongoRepository<RecommendationLog, String> {
    List<RecommendationLog> findByUserIdOrderByGeneratedAtDesc(Long userId);
}
