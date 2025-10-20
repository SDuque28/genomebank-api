package com.backEnd.genomebank.entities;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@Table(name = "chromosomes")
public class Chromosome {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chromosome_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "genome_id", nullable = false)
    private Genome genome;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false)
    private Integer length;

    @Column(columnDefinition = "TEXT")
    private String sequence;

    @Column(name = "created_at", updatable = false, insertable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "chromosome", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Gene> genes = new HashSet<>();
}
