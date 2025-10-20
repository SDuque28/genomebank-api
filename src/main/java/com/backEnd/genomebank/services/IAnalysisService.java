package com.backEnd.genomebank.services;

import com.backEnd.genomebank.dto.analysis.*;
import java.util.List;
import java.util.Optional;

public interface IAnalysisService {
    List<GeneRangeDTO> obtenerGenesPorRango(Long chromosomeId, Integer start, Integer end);
    Optional<SequenceStatsDTO> obtenerEstadisticasSecuencia(Long chromosomeId);
}