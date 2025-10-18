package com.backEnd.genomebank.entities;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "gene_function")
public class GeneFunction {
    @EmbeddedId
    private GeneFunctionId id = new GeneFunctionId();

    @ManyToOne
    @MapsId("geneId")
    @JoinColumn(name = "gene_id")
    private Gene gene;

    @ManyToOne
    @MapsId("functionId")
    @JoinColumn(name = "function_id")
    private Function function;

    @Column(length = 50)
    private String evidence;

    @Column(name = "created_at", updatable = false, insertable = false)
    private LocalDateTime createdAt;
}