package com.backEnd.genomebank.services;

import com.backEnd.genomebank.dto.genome.*;
import java.util.List;
import java.util.Optional;

public interface IGenomeService {
    GenomeOutDTO crearGenome(GenomeInDTO genomeInDTO);
    List<GenomeOutDTO> obtenerTodosGenomes();
    List<GenomeOutDTO> obtenerGenomesPorSpecies(Long speciesId);
    Optional<GenomeOutDTO> obtenerGenomePorId(Long id);
    Optional<GenomeOutDTO> actualizarGenome(Long id, GenomeUpdateDTO genomeUpdateDTO);
    boolean eliminarGenome(Long id);
}