package com.backEnd.genomebank.services.impl;

import com.backEnd.genomebank.dto.chromosome.*;
import com.backEnd.genomebank.dto.genome.GenomeOutDTO;
import com.backEnd.genomebank.dto.species.SpeciesOutDTO;
import com.backEnd.genomebank.entities.Chromosome;
import com.backEnd.genomebank.entities.Genome;
import com.backEnd.genomebank.repositories.ChromosomeRepository;
import com.backEnd.genomebank.repositories.GenomeRepository;
import com.backEnd.genomebank.services.IChromosomeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChromosomeServiceImpl implements IChromosomeService {

    private final ChromosomeRepository chromosomeRepository;
    private final GenomeRepository genomeRepository;
    /**
     * Crear un nuevo Chromosome asociado a un Genome existente.
     * @param chromosomeInDTO Datos de entrada para crear el Chromosome.
     * @return Datos de salida del Chromosome creado.
     */
    @Override
    @Transactional
    public ChromosomeOutDTO crearChromosome(ChromosomeInDTO chromosomeInDTO) {
        Genome genome = genomeRepository.findById(chromosomeInDTO.getGenomeId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Genome not found"));

        // Validar que la longitud coincida con la secuencia si se proporciona
        if (chromosomeInDTO.getSequence() != null) {
            int sequenceLength = chromosomeInDTO.getSequence().length();
            if (!chromosomeInDTO.getLength().equals(sequenceLength)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Length must match sequence length");
            }
        }

        Chromosome chromosome = new Chromosome();
        chromosome.setGenome(genome);
        chromosome.setName(chromosomeInDTO.getName());
        chromosome.setLength(chromosomeInDTO.getLength());
        chromosome.setSequence(chromosomeInDTO.getSequence());
        Chromosome savedChromosome = chromosomeRepository.save(chromosome);
        return convertToOutDTO(savedChromosome);
    }
    /**
     * Obtener todos los Chromosomes.
     * @return Lista de todos los Chromosomes.
     */
    @Override
    @Transactional(readOnly = true)
    public List<ChromosomeOutDTO> obtenerTodosChromosomes() {
        return chromosomeRepository.findAll().stream()
                .map(this::convertToOutDTO)
                .toList();
    }
    /**
     * Obtener todos los Chromosomes por Genome ID.
     * @param genomeId ID del Genome.
     * @return Lista de Chromosomes asociados al Genome.
     */
    @Override
    @Transactional(readOnly = true)
    public List<ChromosomeOutDTO> obtenerChromosomesPorGenome(Long genomeId) {
        return chromosomeRepository.findByGenomeId(genomeId).stream()
                .map(this::convertToOutDTO)
                .toList();
    }
    /**
     * Obtener un Chromosome por ID.
     * @param id ID del Chromosome.
     * @return Chromosome encontrado si existe.
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<ChromosomeOutDTO> obtenerChromosomePorId(Long id) {
        return chromosomeRepository.findById(id)
                .map(this::convertToOutDTO);
    }
    /**
     * Actualizar un Chromosome existente.
     * @param id ID del Chromosome a actualizar.
     * @param chromosomeUpdateDTO Datos de actualización del Chromosome.
     * @return Chromosome actualizado si existe.
     */
    @Override
    @Transactional
    public Optional<ChromosomeOutDTO> actualizarChromosome(Long id, ChromosomeUpdateDTO chromosomeUpdateDTO) {
        return chromosomeRepository.findById(id).map(chromosome -> {
            if (chromosomeUpdateDTO.getGenomeId() != null) {
                Genome genome = genomeRepository.findById(chromosomeUpdateDTO.getGenomeId())
                        .orElseThrow(() -> new ResponseStatusException(
                                HttpStatus.NOT_FOUND, "Genome not found"));
                chromosome.setGenome(genome);
            }
            if (chromosomeUpdateDTO.getName() != null) {
                chromosome.setName(chromosomeUpdateDTO.getName());
            }
            if (chromosomeUpdateDTO.getLength() != null) {
                chromosome.setLength(chromosomeUpdateDTO.getLength());
            }
            Chromosome updatedChromosome = chromosomeRepository.save(chromosome);
            return convertToOutDTO(updatedChromosome);
        });
    }
    /**
     * Eliminar un Chromosome por su ID.
     * @param id ID del Chromosome a eliminar.
     * @return true si el Chromosome fue eliminado, false si no se encontró.
     */
    @Override
    @Transactional
    public boolean eliminarChromosome(Long id) {
        if (chromosomeRepository.existsById(id)) {
            chromosomeRepository.deleteById(id);
            return true;
        }
        return false;
    }
    /**
     * Obtener la secuencia completa de un Chromosome.
     * @param chromosomeId ID del Chromosome.
     * @return Secuencia completa si el Chromosome existe.
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<String> obtenerSecuenciaCompleta(Long chromosomeId) {
        return chromosomeRepository.findById(chromosomeId)
                .map(Chromosome::getSequence);
    }
    /**
     * Obtener una subsequence de un Chromosome dado un rango.
     * @param chromosomeId ID del Chromosome.
     * @param start Posición inicial (inclusive).
     * @param end Posición final (exclusive).
     * @return Secuencia en el rango especificado si el Chromosome existe.
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<String> obtenerSecuenciaPorRango(Long chromosomeId, Integer start, Integer end) {
        return chromosomeRepository.findById(chromosomeId).map(chromosome -> {
            String sequence = chromosome.getSequence();
            if (sequence == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Sequence not available");
            }
            if (start < 0 || end > sequence.length() || start >= end) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Invalid range");
            }
            return sequence.substring(start, end);
        });
    }
    /**
     * Actualizar la secuencia de un Chromosome.
     * @param chromosomeId ID del Chromosome.
     * @param sequence Nueva secuencia para el Chromosome.
     * @return Datos de salida del Chromosome actualizado si se encuentra, opcionalmente.
     */
    @Override
    @Transactional
    public Optional<ChromosomeOutDTO> actualizarSecuencia(Long chromosomeId, String sequence) {
        return chromosomeRepository.findById(chromosomeId).map(chromosome -> {
            if (sequence.length() != chromosome.getLength()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Sequence length must match chromosome length");
            }
            chromosome.setSequence(sequence);
            Chromosome updatedChromosome = chromosomeRepository.save(chromosome);
            return convertToOutDTO(updatedChromosome);
        });
    }
    /**
     * Convertir una entidad Chromosome a ChromosomeOutDTO.
     * @param chromosome Entidad Chromosome.
     * @return DTO de salida ChromosomeOutDTO.
     */
    private ChromosomeOutDTO convertToOutDTO(Chromosome chromosome) {
        ChromosomeOutDTO dto = new ChromosomeOutDTO();
        dto.setId(chromosome.getId());
        dto.setName(chromosome.getName());
        dto.setLength(chromosome.getLength());
        dto.setCreatedAt(chromosome.getCreatedAt());

        // Nested Genome DTO
        GenomeOutDTO genomeDTO = new GenomeOutDTO();
        genomeDTO.setId(chromosome.getGenome().getId());
        genomeDTO.setVersion(chromosome.getGenome().getVersion());
        genomeDTO.setDescription(chromosome.getGenome().getDescription());
        genomeDTO.setCreatedAt(chromosome.getGenome().getCreatedAt());

        // Nested Species in Genome
        SpeciesOutDTO speciesDTO = new SpeciesOutDTO();
        speciesDTO.setId(chromosome.getGenome().getSpecies().getId());
        speciesDTO.setScientificName(chromosome.getGenome().getSpecies().getScientificName());
        speciesDTO.setCommonName(chromosome.getGenome().getSpecies().getCommonName());
        speciesDTO.setCreatedAt(chromosome.getGenome().getSpecies().getCreatedAt());
        speciesDTO.setDescription(chromosome.getGenome().getSpecies().getDescription());
        genomeDTO.setSpecies(speciesDTO);

        dto.setGenome(genomeDTO);

        return dto;
    }
}