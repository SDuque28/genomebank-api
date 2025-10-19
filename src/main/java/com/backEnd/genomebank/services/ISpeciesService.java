package com.backEnd.genomebank.services;

import com.backEnd.genomebank.dto.species.*;
import java.util.List;
import java.util.Optional;

public interface ISpeciesService {
    SpeciesOutDTO crearSpecies(SpeciesInDTO speciesInDTO);
    List<SpeciesOutDTO> obtenerTodasSpecies();
    Optional<SpeciesOutDTO> obtenerSpeciesPorId(Long id);
    Optional<SpeciesOutDTO> actualizarSpecies(Long id, SpeciesUpdateDTO speciesUpdateDTO);
    boolean eliminarSpecies(Long id);
}