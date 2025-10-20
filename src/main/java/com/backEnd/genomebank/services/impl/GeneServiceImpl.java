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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GeneServiceImpl implements IGeneService {

    private final GeneRepository geneRepository;
    private final ChromosomeRepository chromosomeRepository;
    /**
     * Crear un nuevo Gene asociado a un Chromosome existente.
     * @param geneInDTO Datos de entrada para crear el Gene.
     * @return Datos de salida del Gene creado.
     */
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
        gene.setCreatedAt(LocalDateTime.now());

        Gene savedGene = geneRepository.save(gene);
        return convertToOutDTO(savedGene);
    }
    /**
     * Obtener todos los Genes.
     * @return Lista de datos de salida de todos los Genes.
     */
    @Override
    @Transactional(readOnly = true)
    public List<GeneOutDTO> obtenerTodosGenes() {
        return geneRepository.findAll().stream()
                .map(this::convertToOutDTO)
                .toList();
    }
    /**
     * Obtener Genes por ID de Chromosome.
     * @param chromosomeId ID del Chromosome.
     * @return Lista de datos de salida de los Genes asociados al Chromosome.
     */
    @Override
    @Transactional(readOnly = true)
    public List<GeneOutDTO> obtenerGenesPorChromosome(Long chromosomeId) {
        return geneRepository.findByChromosomeId(chromosomeId).stream()
                .map(this::convertToOutDTO)
                .toList();
    }
    /**
     * Obtener Genes por símbolo (búsqueda parcial, case insensitive).
     * @param symbol Símbolo o parte del símbolo del Gene.
     * @return Lista de datos de salida de los Genes que coinciden con el símbolo.
     */
    @Override
    @Transactional(readOnly = true)
    public List<GeneOutDTO> obtenerGenesPorSymbol(String symbol) {
        return geneRepository.findBySymbolContainingIgnoreCase(symbol).stream()
                .map(this::convertToOutDTO)
                .toList();
    }
    /**
     * Obtener Genes en un rango específico dentro de un Chromosome.
     * @param chromosomeId ID del Chromosome.
     * @param start Posición inicial del rango.
     * @param end Posición final del rango.
     * @return Lista de datos de salida de los Genes dentro del rango especificado.
     */
    @Override
    @Transactional(readOnly = true)
    public List<GeneOutDTO> obtenerGenesPorRango(Long chromosomeId, Integer start, Integer end) {
        return geneRepository.findGenesInRange(chromosomeId, start, end).stream()
                .map(this::convertToOutDTO)
                .toList();
    }
    /**
     * Obtener un Gene por su ID.
     * @param id ID del Gene.
     * @return Datos de salida del Gene si se encuentra, opcionalmente.
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<GeneOutDTO> obtenerGenePorId(Long id) {
        return geneRepository.findById(id)
                .map(this::convertToOutDTO);
    }
    /**
     * Actualizar un Gene existente.
     * @param id ID del Gene a actualizar.
     * @param geneUpdateDTO Datos de actualización del Gene.
     * @return Datos de salida del Gene actualizado si se encuentra, opcionalmente.
     */
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
    /**
     * Eliminar un Gene por su ID.
     * @param id ID del Gene a eliminar.
     * @return true si el Gene fue eliminado, false si no se encontró.
     */
    @Override
    @Transactional
    public boolean eliminarGene(Long id) {
        if (geneRepository.existsById(id)) {
            geneRepository.deleteById(id);
            return true;
        }
        return false;
    }
    /**
     * Obtener la secuencia de un Gene por su ID.
     * @param geneId ID del Gene.
     * @return Secuencia del Gene si se encuentra, opcionalmente.
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<String> obtenerSecuenciaGene(Long geneId) {
        return geneRepository.findById(geneId)
                .map(Gene::getSequence);
    }
    /**
     * Actualizar la secuencia de un Gene.
     * @param geneId ID del Gene.
     * @param sequence Nueva secuencia para el Gene.
     * @return Datos de salida del Gene actualizado si se encuentra, opcionalmente.
     */
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
    /**
     * Convertir una entidad Gene a GeneOutDTO.
     * @param gene Entidad Gene.
     * @return Datos de salida del Gene.
     */
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