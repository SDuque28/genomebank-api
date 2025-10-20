package com.backEnd.genomebank.controllers;

import com.backEnd.genomebank.dto.genome.*;
import com.backEnd.genomebank.services.IGenomeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/genomes")
@RequiredArgsConstructor
public class GenomeController {

    private final IGenomeService genomeService;
    /**
     * Obtener todos los Genomes, opcionalmente filtrados por speciesId.
     * @param speciesId (opcional) ID de la especie para filtrar.
     * @return Lista de GenomeOutDTO.
     */
    @GetMapping
    public ResponseEntity<List<GenomeOutDTO>> obtenerGenomes(
            @RequestParam(required = false) Long speciesId) {
        if (speciesId != null) {
            return ResponseEntity.ok(genomeService.obtenerGenomesPorSpecies(speciesId));
        }
        return ResponseEntity.ok(genomeService.obtenerTodosGenomes());
    }
    /**
     * Obtener un Genome por su ID.
     * @param id ID del Genome.
     * @return GenomeOutDTO si se encuentra, o 404 si no existe.
     */
    @GetMapping("/{id}")
    public ResponseEntity<GenomeOutDTO> obtenerGenomePorId(@PathVariable Long id) {
        return genomeService.obtenerGenomePorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    /**
     * Crear un nuevo Genome.
     * Solo los usuarios con rol ADMIN pueden realizar esta operación.
     * @param genomeInDTO Datos de entrada para crear el Genome.
     * @return GenomeOutDTO creado con estado 201.
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<GenomeOutDTO> crearGenome(@Valid @RequestBody GenomeInDTO genomeInDTO) {
        GenomeOutDTO created = genomeService.crearGenome(genomeInDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
    /**
     * Actualizar un Genome existente.
     * Solo los usuarios con rol ADMIN pueden realizar esta operación.
     * @param id ID del Genome a actualizar.
     * @param genomeUpdateDTO Datos de actualización.
     * @return GenomeOutDTO actualizada, o 404 si no se encuentra el Genome.
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<GenomeOutDTO> actualizarGenome(
            @PathVariable Long id,
            @Valid @RequestBody GenomeUpdateDTO genomeUpdateDTO) {
        return genomeService.actualizarGenome(id, genomeUpdateDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    /**
     * Eliminar un Genome por su ID.
     * Solo los usuarios con rol ADMIN pueden realizar esta operación.
     * @param id ID del Genome a eliminar.
     * @return Respuesta con estado 204 si se elimina, o 404 si no se encuentra.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> eliminarGenome(@PathVariable Long id) {
        if (genomeService.eliminarGenome(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}