package com.backEnd.genomebank.entities;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@Table(name = "genes")
public class Gene {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "gene_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "chromosome_id", nullable = false)
    private Chromosome chromosome;

    @Column(nullable = false, length = 50)
    private String symbol;

    @Column(name = "start_position", nullable = false)
    private Integer startPosition;

    @Column(name = "end_position", nullable = false)
    private Integer endPosition;

    @Column(nullable = false, length = 1)
    private Character strand;

    @Column(columnDefinition = "TEXT")
    private String sequence;

    @Column(name = "created_at", updatable = false, insertable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "gene", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<GeneFunction> geneFunctions = new HashSet<>();
}