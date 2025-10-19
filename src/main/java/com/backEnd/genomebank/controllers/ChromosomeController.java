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

    @GetMapping
    public ResponseEntity<List<ChromosomeOutDTO>> obtenerChromosomes(
            @RequestParam(required = false) Long genomeId) {
        if (genomeId != null) {
            return ResponseEntity.ok(chromosomeService.obtenerChromosomesPorGenome(genomeId));
        }
        return ResponseEntity.ok(chromosomeService.obtenerTodosChromosomes());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ChromosomeOutDTO> obtenerChromosomePorId(@PathVariable Long id) {
        return chromosomeService.obtenerChromosomePorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ChromosomeOutDTO> crearChromosome(
            @Valid @RequestBody ChromosomeInDTO chromosomeInDTO) {
        ChromosomeOutDTO created = chromosomeService.crearChromosome(chromosomeInDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ChromosomeOutDTO> actualizarChromosome(
            @PathVariable Long id,
            @Valid @RequestBody ChromosomeUpdateDTO chromosomeUpdateDTO) {
        return chromosomeService.actualizarChromosome(id, chromosomeUpdateDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> eliminarChromosome(@PathVariable Long id) {
        if (chromosomeService.eliminarChromosome(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    // ===== SEQUENCE ENDPOINTS =====

    @GetMapping("/{id}/sequence")
    public ResponseEntity<Map<String, String>> obtenerSecuenciaCompleta(@PathVariable Long id) {
        return chromosomeService.obtenerSecuenciaCompleta(id)
                .map(sequence -> ResponseEntity.ok(Map.of(SEQUENCE, sequence)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/sequence/range")
    public ResponseEntity<Map<String, String>> obtenerSecuenciaPorRango(
            @PathVariable Long id,
            @RequestParam Integer start,
            @RequestParam Integer end) {
        return chromosomeService.obtenerSecuenciaPorRango(id, start, end)
                .map(sequence -> ResponseEntity.ok(Map.of(SEQUENCE, sequence)))
                .orElse(ResponseEntity.notFound().build());
    }

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