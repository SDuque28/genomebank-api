package com.backEnd.genomebank.controllers;

import com.backEnd.genomebank.dto.analysis.*;
import com.backEnd.genomebank.services.IAnalysisService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/analysis")
@RequiredArgsConstructor
public class AnalysisController {

    private final IAnalysisService analysisService;
    /**
     * Obtener genes dentro de un rango específico en un cromosoma.
     *
     * @param chromosomeId ID del cromosoma.
     * @param start        Posición inicial del rango.
     * @param end          Posición final del rango.
     * @return Lista de GeneRangeDTO dentro del rango especificado.
     */
    @GetMapping("/genes")
    public ResponseEntity<List<GeneRangeDTO>> obtenerGenesPorRango(
            @RequestParam Long chromosomeId,
            @RequestParam Integer start,
            @RequestParam Integer end) {
        return ResponseEntity.ok(analysisService.obtenerGenesPorRango(chromosomeId, start, end));
    }
    /**
     * Obtener estadísticas de la secuencia de un cromosoma.
     *
     * @param chromosomeId ID del cromosoma.
     * @return SequenceStatsDTO con las estadísticas de la secuencia, o 404 si no se encuentra.
     */
    @GetMapping("/sequence/stats")
    public ResponseEntity<SequenceStatsDTO> obtenerEstadisticasSecuencia(
            @RequestParam Long chromosomeId) {
        return analysisService.obtenerEstadisticasSecuencia(chromosomeId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}