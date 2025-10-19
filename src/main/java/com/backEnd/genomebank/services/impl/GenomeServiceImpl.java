package com.backEnd.genomebank.services.impl;

import com.backEnd.genomebank.dto.genome.*;
import com.backEnd.genomebank.dto.species.SpeciesOutDTO;
import com.backEnd.genomebank.entities.Genome;
import com.backEnd.genomebank.entities.Species;
import com.backEnd.genomebank.repositories.GenomeRepository;
import com.backEnd.genomebank.repositories.SpeciesRepository;
import com.backEnd.genomebank.services.IGenomeService;
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
public class GenomeServiceImpl implements IGenomeService {

    private final GenomeRepository genomeRepository;
    private final SpeciesRepository speciesRepository;

    @Override
    @Transactional
    public GenomeOutDTO crearGenome(GenomeInDTO genomeInDTO) {
        Species species = speciesRepository.findById(genomeInDTO.getSpeciesId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Species not found"));

        Genome genome = new Genome();
        genome.setSpecies(species);
        genome.setVersion(genomeInDTO.getVersion());
        genome.setDescription(genomeInDTO.getDescription());

        Genome savedGenome = genomeRepository.save(genome);
        return convertToOutDTO(savedGenome);
    }

    @Override
    @Transactional(readOnly = true)
    public List<GenomeOutDTO> obtenerTodosGenomes() {
        return genomeRepository.findAll().stream()
                .map(this::convertToOutDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<GenomeOutDTO> obtenerGenomesPorSpecies(Long speciesId) {
        return genomeRepository.findBySpeciesId(speciesId).stream()
                .map(this::convertToOutDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<GenomeOutDTO> obtenerGenomePorId(Long id) {
        return genomeRepository.findById(id)
                .map(this::convertToOutDTO);
    }

    @Override
    @Transactional
    public Optional<GenomeOutDTO> actualizarGenome(Long id, GenomeUpdateDTO genomeUpdateDTO) {
        return genomeRepository.findById(id).map(genome -> {
            if (genomeUpdateDTO.getSpeciesId() != null) {
                Species species = speciesRepository.findById(genomeUpdateDTO.getSpeciesId())
                        .orElseThrow(() -> new ResponseStatusException(
                                HttpStatus.NOT_FOUND, "Species not found"));
                genome.setSpecies(species);
            }
            if (genomeUpdateDTO.getVersion() != null) {
                genome.setVersion(genomeUpdateDTO.getVersion());
            }
            if (genomeUpdateDTO.getDescription() != null) {
                genome.setDescription(genomeUpdateDTO.getDescription());
            }
            Genome updatedGenome = genomeRepository.save(genome);
            return convertToOutDTO(updatedGenome);
        });
    }

    @Override
    @Transactional
    public boolean eliminarGenome(Long id) {
        if (genomeRepository.existsById(id)) {
            genomeRepository.deleteById(id);
            return true;
        }
        return false;
    }

    private GenomeOutDTO convertToOutDTO(Genome genome) {
        GenomeOutDTO dto = new GenomeOutDTO();
        dto.setId(genome.getId());
        dto.setVersion(genome.getVersion());
        dto.setDescription(genome.getDescription());
        dto.setCreatedAt(genome.getCreatedAt());

        // Nested Species DTO
        SpeciesOutDTO speciesDTO = new SpeciesOutDTO();
        speciesDTO.setId(genome.getSpecies().getId());
        speciesDTO.setScientificName(genome.getSpecies().getScientificName());
        speciesDTO.setCommonName(genome.getSpecies().getCommonName());
        speciesDTO.setDescription(genome.getSpecies().getDescription());
        speciesDTO.setCreatedAt(genome.getSpecies().getCreatedAt());
        dto.setSpecies(speciesDTO);

        return dto;
    }
}