package com.backEnd.genomebank.services.impl;

import com.backEnd.genomebank.dto.gene.*;
import com.backEnd.genomebank.entities.Chromosome;
import com.backEnd.genomebank.entities.Gene;
import com.backEnd.genomebank.repositories.ChromosomeRepository;
import com.backEnd.genomebank.repositories.GeneRepository;
import com.backEnd.genomebank.services.IGeneService;
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
public class GeneServiceImpl implements IGeneService {

    private final GeneRepository geneRepository;
    private final ChromosomeRepository chromosomeRepository;

    @Override
    @Transactional
    public GeneOutDTO crearGene(GeneInDTO geneInDTO) {
        Chromosome chromosome = chromosomeRepository.findById(geneInDTO.getChromosomeId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Chromosome not found"));

        // Validar que las posiciones estén dentro del cromosoma
        if (geneInDTO.getStartPosition() >= geneInDTO.getEndPosition()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Start position must be less than end position");
        }
        if (geneInDTO.getEndPosition() > chromosome.getLength()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Gene positions exceed chromosome length");
        }

        Gene gene = new Gene();
        gene.setChromosome(chromosome);
        gene.setSymbol(geneInDTO.getSymbol());
        gene.setStartPosition(geneInDTO.getStartPosition());
        gene.setEndPosition(geneInDTO.getEndPosition());
        gene.setStrand(geneInDTO.getStrand().charAt(0));
        gene.setSequence(geneInDTO.getSequence());

        Gene savedGene = geneRepository.save(gene);
        return convertToOutDTO(savedGene);
    }

    @Override
    @Transactional(readOnly = true)
    public List<GeneOutDTO> obtenerTodosGenes() {
        return geneRepository.findAll().stream()
                .map(this::convertToOutDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<GeneOutDTO> obtenerGenesPorChromosome(Long chromosomeId) {
        return geneRepository.findByChromosomeId(chromosomeId).stream()
                .map(this::convertToOutDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<GeneOutDTO> obtenerGenesPorSymbol(String symbol) {
        return geneRepository.findBySymbolContainingIgnoreCase(symbol).stream()
                .map(this::convertToOutDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<GeneOutDTO> obtenerGenesPorRango(Long chromosomeId, Integer start, Integer end) {
        return geneRepository.findGenesInRange(chromosomeId, start, end).stream()
                .map(this::convertToOutDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<GeneOutDTO> obtenerGenePorId(Long id) {
        return geneRepository.findById(id)
                .map(this::convertToOutDTO);
    }

    @Override
    @Transactional
    public Optional<GeneOutDTO> actualizarGene(Long id, GeneUpdateDTO geneUpdateDTO) {
        return geneRepository.findById(id).map(gene -> {
            if (geneUpdateDTO.getChromosomeId() != null) {
                Chromosome chromosome = chromosomeRepository.findById(geneUpdateDTO.getChromosomeId())
                        .orElseThrow(() -> new ResponseStatusException(
                                HttpStatus.NOT_FOUND, "Chromosome not found"));
                gene.setChromosome(chromosome);
            }
            if (geneUpdateDTO.getSymbol() != null) {
                gene.setSymbol(geneUpdateDTO.getSymbol());
            }
            if (geneUpdateDTO.getStartPosition() != null) {
                gene.setStartPosition(geneUpdateDTO.getStartPosition());
            }
            if (geneUpdateDTO.getEndPosition() != null) {
                gene.setEndPosition(geneUpdateDTO.getEndPosition());
            }
            if (geneUpdateDTO.getStrand() != null) {
                gene.setStrand(geneUpdateDTO.getStrand().charAt(0));
            }
            Gene updatedGene = geneRepository.save(gene);
            return convertToOutDTO(updatedGene);
        });
    }

    @Override
    @Transactional
    public boolean eliminarGene(Long id) {
        if (geneRepository.existsById(id)) {
            geneRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<String> obtenerSecuenciaGene(Long geneId) {
        return geneRepository.findById(geneId)
                .map(Gene::getSequence);
    }

    @Override
    @Transactional
    public Optional<GeneOutDTO> actualizarSecuenciaGene(Long geneId, String sequence) {
        return geneRepository.findById(geneId).map(gene -> {
            int expectedLength = gene.getEndPosition() - gene.getStartPosition();
            if (sequence.length() != expectedLength) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Sequence length must match gene length (end - start)");
            }
            gene.setSequence(sequence);
            Gene updatedGene = geneRepository.save(gene);
            return convertToOutDTO(updatedGene);
        });
    }

    private GeneOutDTO convertToOutDTO(Gene gene) {
        GeneOutDTO dto = new GeneOutDTO();
        dto.setId(gene.getId());
        dto.setChromosomeId(gene.getChromosome().getId());
        dto.setChromosomeName(gene.getChromosome().getName());
        dto.setSymbol(gene.getSymbol());
        dto.setStartPosition(gene.getStartPosition());
        dto.setEndPosition(gene.getEndPosition());
        dto.setStrand(gene.getStrand());
        dto.setCreatedAt(gene.getCreatedAt());
        return dto;
    }
}