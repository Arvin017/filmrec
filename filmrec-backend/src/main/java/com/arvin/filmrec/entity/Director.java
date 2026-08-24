package com.arvin.filmrec.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "directors")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Director {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // TMDB "person" id
    @Column(unique = true)
    private Integer tmdbId;

    @Column(nullable = false)
    private String name;

    @JsonIgnore
    @Builder.Default
    @ManyToMany(mappedBy = "directors")
    private Set<Movie> movies = new HashSet<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Director other)) return false;
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
