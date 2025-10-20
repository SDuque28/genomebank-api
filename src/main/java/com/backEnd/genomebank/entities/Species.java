package com.backEnd.genomebank.entities;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@Table(name = "species")
public class Species {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "species_id")
    private Long id;

    @Column(name = "scientific_name", nullable = false, length = 100)
    private String scientificName;

    @Column(name = "common_name", length = 100)
    private String commonName;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "created_at", updatable = false, insertable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "species", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Genome> genomes = new HashSet<>();
}
