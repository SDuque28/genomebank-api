package com.backEnd.genomebank.dto.analysis;

import lombok.Data;

@Data
public class SequenceStatsDTO {
    private Long chromosomeId;
    private String chromosomeName;
    private Integer sequenceLength;
    private Integer geneCount;
    private Double gcPercentage; // % de G y C en la secuencia
    private Integer aCount;
    private Integer cCount;
    private Integer gCount;
    private Integer tCount;
    private Integer nCount; // bases desconocidas
}