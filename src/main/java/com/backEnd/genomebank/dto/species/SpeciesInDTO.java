package com.backEnd.genomebank.dto.species;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SpeciesInDTO {
    @NotBlank(message = "Scientific name is required")
    @Size(max = 100, message = "Scientific name must not exceed 100 characters")
    private String scientificName;

    @Size(max = 100, message = "Common name must not exceed 100 characters")
    private String commonName;

    private String description;
}