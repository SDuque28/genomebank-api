package com.backEnd.genomebank.controllers;

import com.backEnd.genomebank.dto.species.*;
import com.backEnd.genomebank.services.ISpeciesService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/species")
@RequiredArgsConstructor
public class SpeciesController {

    private final ISpeciesService speciesService;
    /**
     * Obtener todas las Species.
     * @return Lista de SpeciesOutDTO.
     */
    @GetMapping
    public ResponseEntity<List<SpeciesOutDTO>> obtenerTodasSpecies() {
        return ResponseEntity.ok(speciesService.obtenerTodasSpecies());
    }
    /**
     * Obtener una Species por su ID.
     * @param id ID de la Species.
     * @return SpeciesOutDTO si se encuentra, o 404 si no existe.
     */
    @GetMapping("/{id}")
    public ResponseEntity<SpeciesOutDTO> obtenerSpeciesPorId(@PathVariable Long id) {
        return speciesService.obtenerSpeciesPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    /**
     * Crear una nueva Species.
     * Solo los usuarios con rol ADMIN pueden realizar esta operación.
     * @param speciesInDTO Datos de entrada para crear la Species.
     * @return SpeciesOutDTO creada con estado 201.
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SpeciesOutDTO> crearSpecies(@Valid @RequestBody SpeciesInDTO speciesInDTO) {
        SpeciesOutDTO created = speciesService.crearSpecies(speciesInDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
    /**
     * Actualizar una Species existente.
     * Solo los usuarios con rol ADMIN pueden realizar esta operación.
     * @param id ID de la Species a actualizar.
     * @param speciesUpdateDTO Datos de actualización.
     * @return SpeciesOutDTO actualizada, o 404 si no se encuentra la Species.
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SpeciesOutDTO> actualizarSpecies(
            @PathVariable Long id,
            @Valid @RequestBody SpeciesUpdateDTO speciesUpdateDTO) {
        return speciesService.actualizarSpecies(id, speciesUpdateDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    /**
     * Eliminar una Species por su ID.
     * Solo los usuarios con rol ADMIN pueden realizar esta operación.
     * @param id ID de la Species a eliminar.
     * @return ResponseEntity con estado 204 si se elimina correctamente,
     *         o 404 si no se encuentra la Species.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> eliminarSpecies(@PathVariable Long id) {
        if (speciesService.eliminarSpecies(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}