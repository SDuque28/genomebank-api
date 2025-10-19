package com.backEnd.genomebank.services.impl;

import com.backEnd.genomebank.dto.species.*;
import com.backEnd.genomebank.entities.Species;
import com.backEnd.genomebank.repositories.SpeciesRepository;
import com.backEnd.genomebank.services.ISpeciesService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SpeciesServiceImpl implements ISpeciesService {

    private final SpeciesRepository speciesRepository;

    @Override
    @Transactional
    public SpeciesOutDTO crearSpecies(SpeciesInDTO speciesInDTO) {
        Species species = new Species();
        species.setScientificName(speciesInDTO.getScientificName());
        species.setCommonName(speciesInDTO.getCommonName());
        species.setDescription(speciesInDTO.getDescription());

        Species savedSpecies = speciesRepository.save(species);
        return convertToOutDTO(savedSpecies);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SpeciesOutDTO> obtenerTodasSpecies() {
        return speciesRepository.findAll().stream()
                .map(this::convertToOutDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<SpeciesOutDTO> obtenerSpeciesPorId(Long id) {
        return speciesRepository.findById(id)
                .map(this::convertToOutDTO);
    }

    @Override
    @Transactional
    public Optional<SpeciesOutDTO> actualizarSpecies(Long id, SpeciesUpdateDTO speciesUpdateDTO) {
        return speciesRepository.findById(id).map(species -> {
            if (speciesUpdateDTO.getScientificName() != null) {
                species.setScientificName(speciesUpdateDTO.getScientificName());
            }
            if (speciesUpdateDTO.getCommonName() != null) {
                species.setCommonName(speciesUpdateDTO.getCommonName());
            }
            if (speciesUpdateDTO.getDescription() != null) {
                species.setDescription(speciesUpdateDTO.getDescription());
            }
            Species updatedSpecies = speciesRepository.save(species);
            return convertToOutDTO(updatedSpecies);
        });
    }

    @Override
    @Transactional
    public boolean eliminarSpecies(Long id) {
        if (speciesRepository.existsById(id)) {
            speciesRepository.deleteById(id);
            return true;
        }
        return false;
    }

    private SpeciesOutDTO convertToOutDTO(Species species) {
        SpeciesOutDTO dto = new SpeciesOutDTO();
        dto.setId(species.getId());
        dto.setScientificName(species.getScientificName());
        dto.setCommonName(species.getCommonName());
        dto.setDescription(species.getDescription());
        dto.setCreatedAt(species.getCreatedAt());
        return dto;
    }
}