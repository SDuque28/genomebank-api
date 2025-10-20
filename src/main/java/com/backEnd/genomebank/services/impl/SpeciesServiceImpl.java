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

@Service
@RequiredArgsConstructor
public class SpeciesServiceImpl implements ISpeciesService {

    private final SpeciesRepository speciesRepository;
    /**
     * Crear una nueva Species.
     * @param speciesInDTO Datos de entrada para crear la Species.
     * @return Datos de salida de la Species creada.
     */
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
    /**
     * Obtener todas las Species.
     * @return Lista de todas las Species.
     */
    @Override
    @Transactional(readOnly = true)
    public List<SpeciesOutDTO> obtenerTodasSpecies() {
        return speciesRepository.findAll().stream()
                .map(this::convertToOutDTO)
                .toList();
    }
    /**
     * Obtener una Species por ID.
     * @param id ID de la Species.
     * @return Species encontrada si existe.
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<SpeciesOutDTO> obtenerSpeciesPorId(Long id) {
        return speciesRepository.findById(id)
                .map(this::convertToOutDTO);
    }
    /**
     * Actualizar una Species existente.
     * @param id ID de la Species a actualizar.
     * @param speciesUpdateDTO Datos de actualización de la Species.
     * @return Species actualizada si existe.
     */
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
    /**
     * Eliminar una Species por ID.
     * @param id ID de la Species a eliminar.
     * @return true si la Species fue eliminada, false si no existe.
     */
    @Override
    @Transactional
    public boolean eliminarSpecies(Long id) {
        if (speciesRepository.existsById(id)) {
            speciesRepository.deleteById(id);
            return true;
        }
        return false;
    }
    /**
     * Convertir una entidad Species a SpeciesOutDTO.
     * @param species Entidad Species.
     * @return DTO de salida SpeciesOutDTO.
     */
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