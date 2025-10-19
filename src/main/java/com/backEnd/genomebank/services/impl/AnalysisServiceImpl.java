package com.backEnd.genomebank.services.impl;

import com.backEnd.genomebank.dto.analysis.*;
import com.backEnd.genomebank.entities.Gene;
import com.backEnd.genomebank.repositories.ChromosomeRepository;
import com.backEnd.genomebank.repositories.GeneRepository;
import com.backEnd.genomebank.services.IAnalysisService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnalysisServiceImpl implements IAnalysisService {

    private final GeneRepository geneRepository;
    private final ChromosomeRepository chromosomeRepository;

    @Override
    @Transactional(readOnly = true)
    public List<GeneRangeDTO> obtenerGenesPorRango(Long chromosomeId, Integer start, Integer end) {
        // Verificar que el cromosoma existe
        chromosomeRepository.findById(chromosomeId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Chromosome not found"));

        if (start < 0 || start >= end) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Invalid range: start must be less than end and non-negative");
        }

        return geneRepository.findGenesInRange(chromosomeId, start, end).stream()
                .map(this::convertToRangeDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<SequenceStatsDTO> obtenerEstadisticasSecuencia(Long chromosomeId) {
        return chromosomeRepository.findById(chromosomeId).map(chromosome -> {
            String sequence = chromosome.getSequence();
            if (sequence == null || sequence.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Sequence not available for this chromosome");
            }

            SequenceStatsDTO stats = new SequenceStatsDTO();
            stats.setChromosomeId(chromosome.getId());
            stats.setChromosomeName(chromosome.getName());
            stats.setSequenceLength(sequence.length());

            // Contar genes asociados al cromosoma
            int geneCount = geneRepository.findByChromosomeId(chromosomeId).size();
            stats.setGeneCount(geneCount);

            // Contar bases
            int aCount = 0;
            int cCount = 0;
            int gCount = 0;
            int tCount = 0;
            int nCount = 0;

            for (char base : sequence.toUpperCase().toCharArray()) {
                switch (base) {
                    case 'A': aCount++; break;
                    case 'C': cCount++; break;
                    case 'G': gCount++; break;
                    case 'T': tCount++; break;
                    case 'N': nCount++; break;
                }
            }

            stats.setACount(aCount);
            stats.setCCount(cCount);
            stats.setGCount(gCount);
            stats.setTCount(tCount);
            stats.setNCount(nCount);

            // Calcular porcentaje GC
            int totalValidBases = aCount + cCount + gCount + tCount;
            if (totalValidBases > 0) {
                double gcPercentage = ((double) (gCount + cCount) / totalValidBases) * 100;
                stats.setGcPercentage(Math.round(gcPercentage * 100.0) / 100.0); // 2 decimales
            } else {
                stats.setGcPercentage(0.0);
            }

            return stats;
        });
    }

    private GeneRangeDTO convertToRangeDTO(Gene gene) {
        GeneRangeDTO dto = new GeneRangeDTO();
        dto.setGeneId(gene.getId());
        dto.setSymbol(gene.getSymbol());
        dto.setStartPosition(gene.getStartPosition());
        dto.setEndPosition(gene.getEndPosition());
        dto.setStrand(gene.getStrand());
        dto.setChromosomeName(gene.getChromosome().getName());
        return dto;
    }
}