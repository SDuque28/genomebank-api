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

    @GetMapping
    public ResponseEntity<List<GeneOutDTO>> obtenerGenes(
            @RequestParam(required = false) Long chromosomeId,
            @RequestParam(required = false) Integer start,
            @RequestParam(required = false) Integer end,
            @RequestParam(required = false) String symbol) {

        // Filtrar por rango en cromosoma
        if (chromosomeId != null && start != null && end != null) {
            return ResponseEntity.ok(geneService.obtenerGenesPorRango(chromosomeId, start, end));
        }

        // Filtrar por cromosoma
        if (chromosomeId != null) {
            return ResponseEntity.ok(geneService.obtenerGenesPorChromosome(chromosomeId));
        }

        // Filtrar por símbolo
        if (symbol != null) {
            return ResponseEntity.ok(geneService.obtenerGenesPorSymbol(symbol));
        }

        // Todos los genes
        return ResponseEntity.ok(geneService.obtenerTodosGenes());
    }

    @GetMapping("/{id}")
    public ResponseEntity<GeneOutDTO> obtenerGenePorId(@PathVariable Long id) {
        return geneService.obtenerGenePorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<GeneOutDTO> crearGene(@Valid @RequestBody GeneInDTO geneInDTO) {
        GeneOutDTO created = geneService.crearGene(geneInDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<GeneOutDTO> actualizarGene(
            @PathVariable Long id,
            @Valid @RequestBody GeneUpdateDTO geneUpdateDTO) {
        return geneService.actualizarGene(id, geneUpdateDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> eliminarGene(@PathVariable Long id) {
        if (geneService.eliminarGene(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    // ===== SEQUENCE ENDPOINTS =====

    @GetMapping("/{id}/sequence")
    public ResponseEntity<Map<String, String>> obtenerSecuenciaGene(@PathVariable Long id) {
        return geneService.obtenerSecuenciaGene(id)
                .map(sequence -> ResponseEntity.ok(Map.of("sequence", sequence)))
                .orElse(ResponseEntity.notFound().build());
    }

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