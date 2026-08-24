package com.arvin.filmrec.document;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "recommendation_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecommendationLog {

    @Id
    private String id;

    private Long userId;

    @Builder.Default
    private LocalDateTime generatedAt = LocalDateTime.now();

    private List<RecommendationEntry> recommendations;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class RecommendationEntry {
        private Long movieId;
        private String movieTitle;
        private double score;
        private String reason;
    }
}
