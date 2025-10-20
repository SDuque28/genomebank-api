package com.backEnd.genomebank.dto.genome;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class GenomeInDTO {
    @NotNull(message = "Species ID is required")
    private Long speciesId;

    @NotBlank(message = "Version is required")
    @Size(max = 50, message = "Version must not exceed 50 characters")
    private String version;

    private String description;
}
