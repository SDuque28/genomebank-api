package com.backEnd.genomebank.dto.chromosome;

import com.backEnd.genomebank.dto.genome.GenomeOutDTO;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ChromosomeOutDTO {
    private Long id;
    private GenomeOutDTO genome;
    private String name;
    private Integer length;
    private LocalDateTime createdAt;
}