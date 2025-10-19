package com.backEnd.genomebank.dto.genefunction;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class GeneFunctionOutDTO {
    private Long geneId;
    private String geneSymbol;
    private Long functionId;
    private String functionCode;
    private String functionName;
    private String evidence; // "experimental", "computational", "predicted"
    private LocalDateTime createdAt;
}