package com.backEnd.genomebank.services;

import com.backEnd.genomebank.dto.genefunction.*;
import java.util.List;

public interface IGeneFunctionService {
    GeneFunctionOutDTO asociarFunctionAGene(Long geneId, Long functionId, String evidence);
    List<GeneFunctionOutDTO> obtenerFunctionsPorGene(Long geneId);
    List<GeneFunctionOutDTO> obtenerGenesPorFunction(Long functionId);
    boolean eliminarAsociacion(Long geneId, Long functionId);
}