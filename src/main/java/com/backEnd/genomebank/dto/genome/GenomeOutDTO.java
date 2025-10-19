package com.backEnd.genomebank.dto.genome;

import com.backEnd.genomebank.dto.species.SpeciesOutDTO;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class GenomeOutDTO {
    private Long id;
    private SpeciesOutDTO species; // Nested DTO para mostrar info de la especie
    private String version;
    private String description;
    private LocalDateTime createdAt;
}