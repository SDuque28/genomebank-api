package com.backEnd.genomebank.dto.gene;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class GeneInDTO {
    @NotNull(message = "Chromosome ID is required")
    private Long chromosomeId;

    @NotBlank(message = "Gene symbol is required")
    @Size(max = 50, message = "Symbol must not exceed 50 characters")
    private String symbol;

    @NotNull(message = "Start position is required")
    @Positive(message = "Start position must be positive")
    private Integer startPosition;

    @NotNull(message = "End position is required")
    @Positive(message = "End position must be positive")
    private Integer endPosition;

    @NotNull(message = "Strand is required")
    @Pattern(regexp = "[+\\-]", message = "Strand must be '+' or '-'")
    private String strand;

    private String sequence; // Secuencia de ADN del gen
}