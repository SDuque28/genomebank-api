package com.backEnd.genomebank.controllers;

import com.backEnd.genomebank.dto.function.*;
import com.backEnd.genomebank.entities.Function.Category;
import com.backEnd.genomebank.services.IFunctionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/functions")
@RequiredArgsConstructor
public class FunctionController {

    private final IFunctionService functionService;

    @GetMapping
    public ResponseEntity<List<FunctionOutDTO>> obtenerFunctions(
            @RequestParam(required = false) String code,
            @RequestParam(required = false) Category category) {

        // Filtrar por código
        if (code != null) {
            return ResponseEntity.ok(functionService.obtenerFunctionsPorCode(code));
        }

        // Filtrar por categoría
        if (category != null) {
            return ResponseEntity.ok(functionService.obtenerFunctionsPorCategory(category));
        }

        // Todas las funciones
        return ResponseEntity.ok(functionService.obtenerTodasFunctions());
    }

    @GetMapping("/{id}")
    public ResponseEntity<FunctionOutDTO> obtenerFunctionPorId(@PathVariable Long id) {
        return functionService.obtenerFunctionPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<FunctionOutDTO> crearFunction(@Valid @RequestBody FunctionInDTO functionInDTO) {
        FunctionOutDTO created = functionService.crearFunction(functionInDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<FunctionOutDTO> actualizarFunction(
            @PathVariable Long id,
            @Valid @RequestBody FunctionUpdateDTO functionUpdateDTO) {
        return functionService.actualizarFunction(id, functionUpdateDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> eliminarFunction(@PathVariable Long id) {
        if (functionService.eliminarFunction(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}