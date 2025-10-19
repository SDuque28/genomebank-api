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
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChromosomeServiceImpl implements IChromosomeService {

    private final ChromosomeRepository chromosomeRepository;
    private final GenomeRepository genomeRepository;

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

    @Override
    @Transactional(readOnly = true)
    public List<ChromosomeOutDTO> obtenerTodosChromosomes() {
        return chromosomeRepository.findAll().stream()
                .map(this::convertToOutDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChromosomeOutDTO> obtenerChromosomesPorGenome(Long genomeId) {
        return chromosomeRepository.findByGenomeId(genomeId).stream()
                .map(this::convertToOutDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ChromosomeOutDTO> obtenerChromosomePorId(Long id) {
        return chromosomeRepository.findById(id)
                .map(this::convertToOutDTO);
    }

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

    @Override
    @Transactional
    public boolean eliminarChromosome(Long id) {
        if (chromosomeRepository.existsById(id)) {
            chromosomeRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<String> obtenerSecuenciaCompleta(Long chromosomeId) {
        return chromosomeRepository.findById(chromosomeId)
                .map(Chromosome::getSequence);
    }

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
        genomeDTO.setSpecies(speciesDTO);

        dto.setGenome(genomeDTO);

        return dto;
    }
}