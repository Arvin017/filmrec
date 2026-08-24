package com.arvin.filmrec.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "ratings", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "movie_id"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Rating {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "movie_id", nullable = false)
    private Movie movie;

    @Min(1)
    @Max(5)
    @Column(nullable = false)
    private Integer score;

    @Builder.Default
    @Column(nullable = false)
    private LocalDateTime ratedAt = LocalDateTime.now();
}
