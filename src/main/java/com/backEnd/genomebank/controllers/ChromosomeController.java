package com.backEnd.genomebank.controllers;

import com.backEnd.genomebank.dto.chromosome.*;
import com.backEnd.genomebank.services.IChromosomeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/chromosomes")
@RequiredArgsConstructor
public class ChromosomeController {

    private final IChromosomeService chromosomeService;
    private static final String SEQUENCE = "sequence";
    /**
     * Obtener todos los Chromosomes, con opción de filtrar por genomeId.
     * @param genomeId (opcional) ID del genome para filtrar.
     * @return Lista de ChromosomeOutDTO.
     */
    @GetMapping
    public ResponseEntity<List<ChromosomeOutDTO>> obtenerChromosomes(
            @RequestParam(required = false) Long genomeId) {
        if (genomeId != null) {
            return ResponseEntity.ok(chromosomeService.obtenerChromosomesPorGenome(genomeId));
        }
        return ResponseEntity.ok(chromosomeService.obtenerTodosChromosomes());
    }
    /**
     * Obtener un Chromosome por su ID.
     * @param id ID del Chromosome.
     * @return ChromosomeOutDTO si se encuentra, o 404 si no existe.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ChromosomeOutDTO> obtenerChromosomePorId(@PathVariable Long id) {
        return chromosomeService.obtenerChromosomePorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    /**
     * Crear un nuevo Chromosome.
     * Solo los usuarios con rol ADMIN pueden realizar esta operación.
     * @param chromosomeInDTO Datos de entrada para crear el Chromosome.
     * @return ChromosomeOutDTO creado con estado 201.
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ChromosomeOutDTO> crearChromosome(@Valid @RequestBody ChromosomeInDTO chromosomeInDTO) {
        ChromosomeOutDTO created = chromosomeService.crearChromosome(chromosomeInDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
    /**
     * Actualizar un Chromosome existente.
     * Solo los usuarios con rol ADMIN pueden realizar esta operación.
     * @param id ID del Chromosome a actualizar.
     * @param chromosomeUpdateDTO Datos de actualización.
     * @return ChromosomeOutDTO actualizado, o 404 si no se encuentra el Chromosome.
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ChromosomeOutDTO> actualizarChromosome(
            @PathVariable Long id,
            @Valid @RequestBody ChromosomeUpdateDTO chromosomeUpdateDTO) {
        return chromosomeService.actualizarChromosome(id, chromosomeUpdateDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    /**
     * Eliminar un Chromosome por su ID.
     * Solo los usuarios con rol ADMIN pueden realizar esta operación.
     * @param id ID del Chromosome a eliminar.
     * @return 204 si se elimina, o 404 si no se encuentra el Chromosome.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> eliminarChromosome(@PathVariable Long id) {
        if (chromosomeService.eliminarChromosome(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
    /**
     * Obtener la secuencia completa de un Chromosome por su ID.
     * @param id ID del Chromosome.
     * @return Mapa con la secuencia completa, o 404 si no se encuentra el Chromosome.
     */
    @GetMapping("/{id}/sequence")
    public ResponseEntity<Map<String, String>> obtenerSecuenciaCompleta(@PathVariable Long id) {
        return chromosomeService.obtenerSecuenciaCompleta(id)
                .map(sequence -> ResponseEntity.ok(Map.of(SEQUENCE, sequence)))
                .orElse(ResponseEntity.notFound().build());
    }
    /**
     * Obtener una subsecuencia de un Chromosome por su ID y rango.
     * @param id    ID del Chromosome.
     * @param start Posición inicial de la subsecuencia (inclusive).
     * @param end   Posición final de la subsecuencia (inclusive).
     * @return Mapa con la subsecuencia, o 404 si no se encuentra el Chromosome o el rango es inválido.
     */
    @GetMapping("/{id}/sequence/range")
    public ResponseEntity<Map<String, String>> obtenerSecuenciaPorRango(
            @PathVariable Long id,
            @RequestParam Integer start,
            @RequestParam Integer end) {
        return chromosomeService.obtenerSecuenciaPorRango(id, start, end)
                .map(sequence -> ResponseEntity.ok(Map.of(SEQUENCE, sequence)))
                .orElse(ResponseEntity.notFound().build());
    }
    /**
     * Actualizar la secuencia de un Chromosome por su ID.
     * Solo los usuarios con rol ADMIN pueden realizar esta operación.
     * @param id   ID del Chromosome.
     * @param body Mapa que contiene la nueva secuencia bajo la clave "sequence".
     * @return ChromosomeOutDTO actualizado, o 404 si no se encuentra el Chromosome.
     */
    @PutMapping("/{id}/sequence")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ChromosomeOutDTO> actualizarSecuencia(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        String sequence = body.get(SEQUENCE);
        if (sequence == null) {
            return ResponseEntity.badRequest().build();
        }
        return chromosomeService.actualizarSecuencia(id, sequence)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}