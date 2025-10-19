package com.backEnd.genomebank.dto.genome;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class GenomeUpdateDTO {
    private Long speciesId;

    @Size(max = 50, message = "Version must not exceed 50 characters")
    private String version;

    private String description;
}