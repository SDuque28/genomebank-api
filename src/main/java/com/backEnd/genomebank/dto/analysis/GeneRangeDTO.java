package com.backEnd.genomebank.dto.analysis;

import lombok.Data;

@Data
public class GeneRangeDTO {
    private Long geneId;
    private String symbol;
    private Integer startPosition;
    private Integer endPosition;
    private Character strand;
    private String chromosomeName;
}