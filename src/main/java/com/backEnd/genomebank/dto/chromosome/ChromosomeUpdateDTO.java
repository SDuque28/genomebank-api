package com.backEnd.genomebank.dto.chromosome;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ChromosomeUpdateDTO {
    private Long genomeId;

    @Size(max = 50, message = "Name must not exceed 50 characters")
    private String name;

    @Positive(message = "Length must be positive")
    private Integer length;
}