package com.backEnd.genomebank.controllers;

import com.backEnd.genomebank.dto.genefunction.GeneFunctionOutDTO;
import com.backEnd.genomebank.services.IGeneFunctionService;
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
public class GeneFunctionController {

    private final IGeneFunctionService geneFunctionService;

    @GetMapping("/{id}/functions")
    public ResponseEntity<List<GeneFunctionOutDTO>> obtenerFunctionsPorGene(@PathVariable Long id) {
        return ResponseEntity.ok(geneFunctionService.obtenerFunctionsPorGene(id));
    }

    @PostMapping("/{id}/functions/{functionId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<GeneFunctionOutDTO> asociarFunctionAGene(
            @PathVariable Long id,
            @PathVariable Long functionId,
            @RequestBody(required = false) Map<String, String> body) {

        String evidence = (body != null) ? body.get("evidence") : null;
        GeneFunctionOutDTO created = geneFunctionService.asociarFunctionAGene(id, functionId, evidence);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @DeleteMapping("/{id}/functions/{functionId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> eliminarAsociacion(
            @PathVariable Long id,
            @PathVariable Long functionId) {
        if (geneFunctionService.eliminarAsociacion(id, functionId)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}