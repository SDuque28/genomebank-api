package com.backEnd.genomebank.services;

import com.backEnd.genomebank.dto.chromosome.*;
import java.util.List;
import java.util.Optional;

public interface IChromosomeService {
    ChromosomeOutDTO crearChromosome(ChromosomeInDTO chromosomeInDTO);
    List<ChromosomeOutDTO> obtenerTodosChromosomes();
    List<ChromosomeOutDTO> obtenerChromosomesPorGenome(Long genomeId);
    Optional<ChromosomeOutDTO> obtenerChromosomePorId(Long id);
    Optional<ChromosomeOutDTO> actualizarChromosome(Long id, ChromosomeUpdateDTO chromosomeUpdateDTO);
    boolean eliminarChromosome(Long id);

    // Métodos para gestión de secuencias
    Optional<String> obtenerSecuenciaCompleta(Long chromosomeId);
    Optional<String> obtenerSecuenciaPorRango(Long chromosomeId, Integer start, Integer end);
    Optional<ChromosomeOutDTO> actualizarSecuencia(Long chromosomeId, String sequence);
}