package com.arvin.filmrec.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "genres", uniqueConstraints = @UniqueConstraint(columnNames = "name"))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Genre {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Mirrors TMDB's genre id so we don't duplicate genres on re-seed
    @Column(unique = true)
    private Integer tmdbId;

    @Column(nullable = false, unique = true)
    private String name;

    @JsonIgnore
    @Builder.Default
    @ManyToMany(mappedBy = "genres")
    private Set<Movie> movies = new HashSet<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Genre other)) return false;
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
