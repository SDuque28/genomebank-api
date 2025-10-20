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
    /**
     * Obtener todas las Functions, con opciones de filtrado.
     *
     * @param code     (opcional) Código de la función para filtrar.
     * @param category (opcional) Categoría de la función para filtrar.
     * @return Lista de FunctionOutDTO.
     */
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
    /**
     * Obtener una Function por su ID.
     *
     * @param id ID de la Function.
     * @return FunctionOutDTO si se encuentra, o 404 si no existe.
     */
    @GetMapping("/{id}")
    public ResponseEntity<FunctionOutDTO> obtenerFunctionPorId(@PathVariable Long id) {
        return functionService.obtenerFunctionPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    /**
     * Crear una nueva Function.
     * Solo los usuarios con rol ADMIN pueden realizar esta operación.
     *
     * @param functionInDTO Datos de entrada para crear la Function.
     * @return FunctionOutDTO creada con estado 201.
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<FunctionOutDTO> crearFunction(@Valid @RequestBody FunctionInDTO functionInDTO) {
        FunctionOutDTO created = functionService.crearFunction(functionInDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
    /**
     * Actualizar una Function existente.
     * Solo los usuarios con rol ADMIN pueden realizar esta operación.
     *
     * @param id                ID de la Function a actualizar.
     * @param functionUpdateDTO Datos de actualización.
     * @return FunctionOutDTO actualizada, o 404 si no se encuentra la Function.
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<FunctionOutDTO> actualizarFunction(
            @PathVariable Long id,
            @Valid @RequestBody FunctionUpdateDTO functionUpdateDTO) {
        return functionService.actualizarFunction(id, functionUpdateDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    /**
     * Eliminar una Function por su ID.
     * Solo los usuarios con rol ADMIN pueden realizar esta operación.
     *
     * @param id ID de la Function a eliminar.
     * @return Respuesta HTTP indicando el resultado de la operación.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> eliminarFunction(@PathVariable Long id) {
        if (functionService.eliminarFunction(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}