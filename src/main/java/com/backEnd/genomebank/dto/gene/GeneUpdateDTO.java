package com.backEnd.genomebank.dto.gene;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class GeneUpdateDTO {
    private Long chromosomeId;

    @Size(max = 50, message = "Symbol must not exceed 50 characters")
    private String symbol;

    @Positive(message = "Start position must be positive")
    private Integer startPosition;

    @Positive(message = "End position must be positive")
    private Integer endPosition;

    @Pattern(regexp = "[+\\-]", message = "Strand must be '+' or '-'")
    private String strand;
}