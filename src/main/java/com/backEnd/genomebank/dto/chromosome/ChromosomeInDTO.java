package com.backEnd.genomebank.dto.chromosome;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ChromosomeInDTO {
    @NotNull(message = "Genome ID is required")
    private Long genomeId;

    @NotBlank(message = "Chromosome name is required")
    @Size(max = 50, message = "Name must not exceed 50 characters")
    private String name;

    @NotNull(message = "Length is required")
    @Positive(message = "Length must be positive")
    private Integer length;

    private String sequence; // Secuencia de ADN (ACGTN)
}