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

    @GetMapping
    public ResponseEntity<List<SpeciesOutDTO>> obtenerTodasSpecies() {
        return ResponseEntity.ok(speciesService.obtenerTodasSpecies());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SpeciesOutDTO> obtenerSpeciesPorId(@PathVariable Long id) {
        return speciesService.obtenerSpeciesPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SpeciesOutDTO> crearSpecies(@Valid @RequestBody SpeciesInDTO speciesInDTO) {
        SpeciesOutDTO created = speciesService.crearSpecies(speciesInDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SpeciesOutDTO> actualizarSpecies(
            @PathVariable Long id,
            @Valid @RequestBody SpeciesUpdateDTO speciesUpdateDTO) {
        return speciesService.actualizarSpecies(id, speciesUpdateDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> eliminarSpecies(@PathVariable Long id) {
        if (speciesService.eliminarSpecies(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}