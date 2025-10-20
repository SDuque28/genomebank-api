package com.backEnd.genomebank.controllers;

import com.backEnd.genomebank.dto.gene.*;
import com.backEnd.genomebank.services.IGeneService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/genes")
@RequiredArgsConstructor
public class GeneController {

    private final IGeneService geneService;
    /**
     * Obtener todos los Genes, con opciones de filtrado.
     * @param chromosomeId (opcional) ID del cromosoma para filtrar.
     * @param start (opcional) Posición inicial para filtrar por rango.
     * @param end (opcional) Posición final para filtrar por rango.
     * @param symbol (opcional) Símbolo del gen para filtrar.
     * @return Lista de GeneOutDTO.
     */
    @GetMapping
    public ResponseEntity<List<GeneOutDTO>> obtenerGenes(
            @RequestParam(required = false) Long chromosomeId,
            @RequestParam(required = false) Integer start,
            @RequestParam(required = false) Integer end,
            @RequestParam(required = false) String symbol) {

        if (chromosomeId != null && start != null && end != null) {
            return ResponseEntity.ok(geneService.obtenerGenesPorRango(chromosomeId, start, end));
        }
        if (chromosomeId != null) {
            return ResponseEntity.ok(geneService.obtenerGenesPorChromosome(chromosomeId));
        }
        if (symbol != null) {
            return ResponseEntity.ok(geneService.obtenerGenesPorSymbol(symbol));
        }
        return ResponseEntity.ok(geneService.obtenerTodosGenes());
    }
    /**
     * Obtener un Gene por su ID.
     * @param id ID del Gene.
     * @return GeneOutDTO si se encuentra, o 404 si no existe.
     */
    @GetMapping("/{id}")
    public ResponseEntity<GeneOutDTO> obtenerGenePorId(@PathVariable Long id) {
        return geneService.obtenerGenePorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    /**
     * Crear un nuevo Gene.
     * Solo los usuarios con rol ADMIN pueden realizar esta operación.
     * @param geneInDTO Datos de entrada para crear el Gene.
     * @return GeneOutDTO creado con estado 201.
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<GeneOutDTO> crearGene(@Valid @RequestBody GeneInDTO geneInDTO) {
        GeneOutDTO created = geneService.crearGene(geneInDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
    /**
     * Actualizar un Gene existente.
     * Solo los usuarios con rol ADMIN pueden realizar esta operación.
     * @param id ID del Gene a actualizar.
     * @param geneUpdateDTO Datos de actualización.
     * @return GeneOutDTO actualizada, o 404 si no se encuentra el Gene.
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<GeneOutDTO> actualizarGene(
            @PathVariable Long id,
            @Valid @RequestBody GeneUpdateDTO geneUpdateDTO) {
        return geneService.actualizarGene(id, geneUpdateDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    /**
     * Eliminar un Gene por su ID.
     * Solo los usuarios con rol ADMIN pueden realizar esta operación.
     * @param id ID del Gene a eliminar.
     * @return Respuesta con estado 204 si se elimina, o 404 si no se encuentra.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> eliminarGene(@PathVariable Long id) {
        if (geneService.eliminarGene(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
    /**
     * Obtener la secuencia de un Gene por su ID.
     * @param id ID del Gene.
     * @return Mapa con la secuencia del gen, o 404 si no se encuentra.
     */
    @GetMapping("/{id}/sequence")
    public ResponseEntity<Map<String, String>> obtenerSecuenciaGene(@PathVariable Long id) {
        return geneService.obtenerSecuenciaGene(id)
                .map(sequence -> ResponseEntity.ok(Map.of("sequence", sequence)))
                .orElse(ResponseEntity.notFound().build());
    }
    /**
     * Actualizar la secuencia de un Gene por su ID.
     * Solo los usuarios con rol ADMIN pueden realizar esta operación.
     * @param id ID del Gene.
     * @param body Mapa que contiene la nueva secuencia bajo la clave "sequence".
     * @return GeneOutDTO actualizado, o 404 si no se encuentra el Gene.
     */
    @PutMapping("/{id}/sequence")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<GeneOutDTO> actualizarSecuenciaGene(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        String sequence = body.get("sequence");
        if (sequence == null) {
            return ResponseEntity.badRequest().build();
        }
        return geneService.actualizarSecuenciaGene(id, sequence)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}