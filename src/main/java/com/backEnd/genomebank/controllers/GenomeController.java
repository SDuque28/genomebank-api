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

    @GetMapping
    public ResponseEntity<List<GenomeOutDTO>> obtenerGenomes(
            @RequestParam(required = false) Long speciesId) {
        if (speciesId != null) {
            return ResponseEntity.ok(genomeService.obtenerGenomesPorSpecies(speciesId));
        }
        return ResponseEntity.ok(genomeService.obtenerTodosGenomes());
    }

    @GetMapping("/{id}")
    public ResponseEntity<GenomeOutDTO> obtenerGenomePorId(@PathVariable Long id) {
        return genomeService.obtenerGenomePorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<GenomeOutDTO> crearGenome(@Valid @RequestBody GenomeInDTO genomeInDTO) {
        GenomeOutDTO created = genomeService.crearGenome(genomeInDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<GenomeOutDTO> actualizarGenome(
            @PathVariable Long id,
            @Valid @RequestBody GenomeUpdateDTO genomeUpdateDTO) {
        return genomeService.actualizarGenome(id, genomeUpdateDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> eliminarGenome(@PathVariable Long id) {
        if (genomeService.eliminarGenome(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}