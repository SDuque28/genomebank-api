package com.backEnd.genomebank.dto.gene;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class GeneOutDTO {
    private Long id;
    private Long chromosomeId;
    private String chromosomeName;
    private String symbol;
    private Integer startPosition;
    private Integer endPosition;
    private Character strand;
    private LocalDateTime createdAt;
}