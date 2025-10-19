package com.backEnd.genomebank.dto.species;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class SpeciesOutDTO {
    private Long id;
    private String scientificName;
    private String commonName;
    private String description;
    private LocalDateTime createdAt;
}
