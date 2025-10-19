package com.backEnd.genomebank.entities;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@Table(name = "genomes")
public class Genome {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "genome_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "species_id", nullable = false)
    private Species species;

    @Column(nullable = false, length = 50)
    private String version;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "created_at", updatable = false, insertable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "genome", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Chromosome> chromosomes = new HashSet<>();
}
