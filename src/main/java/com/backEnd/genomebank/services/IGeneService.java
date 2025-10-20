package com.backEnd.genomebank.services;

import com.backEnd.genomebank.dto.gene.*;
import java.util.List;
import java.util.Optional;

public interface IGeneService {
    GeneOutDTO crearGene(GeneInDTO geneInDTO);
    List<GeneOutDTO> obtenerTodosGenes();
    List<GeneOutDTO> obtenerGenesPorChromosome(Long chromosomeId);
    List<GeneOutDTO> obtenerGenesPorSymbol(String symbol);
    List<GeneOutDTO> obtenerGenesPorRango(Long chromosomeId, Integer start, Integer end);
    Optional<GeneOutDTO> obtenerGenePorId(Long id);
    Optional<GeneOutDTO> actualizarGene(Long id, GeneUpdateDTO geneUpdateDTO);
    boolean eliminarGene(Long id);

    // Métodos para gestión de secuencias
    Optional<String> obtenerSecuenciaGene(Long geneId);
    Optional<GeneOutDTO> actualizarSecuenciaGene(Long geneId, String sequence);
}