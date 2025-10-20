package com.backEnd.genomebank.services.impl;

import com.backEnd.genomebank.dto.function.*;
import com.backEnd.genomebank.entities.Function;
import com.backEnd.genomebank.entities.Function.Category;
import com.backEnd.genomebank.repositories.FunctionRepository;
import com.backEnd.genomebank.services.IFunctionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FunctionServiceImpl implements IFunctionService {

    private final FunctionRepository functionRepository;
    /**
     * Crear una nueva Function.
     * @param functionInDTO Datos de entrada para crear la Function.
     * @return Datos de salida de la Function creada.
     */
    @Override
    @Transactional
    public FunctionOutDTO crearFunction(FunctionInDTO functionInDTO) {
        // Validar que el código sea único
        if (functionRepository.findByCode(functionInDTO.getCode()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Function code already exists");
        }

        Function function = new Function();
        function.setCode(functionInDTO.getCode());
        function.setName(functionInDTO.getName());
        function.setCategory(functionInDTO.getCategory());
        function.setDescription(functionInDTO.getDescription());

        Function savedFunction = functionRepository.save(function);
        return convertToOutDTO(savedFunction);
    }
    /**
     * Obtener todas las Functions.
     * @return Lista de todas las Functions.
     */
    @Override
    @Transactional(readOnly = true)
    public List<FunctionOutDTO> obtenerTodasFunctions() {
        return functionRepository.findAll().stream()
                .map(this::convertToOutDTO)
                .toList();
    }
    /**
     * Obtener todas las Functions por código.
     * @param code Código de la Function.
     * @return Lista de Functions que contienen el código especificado.
     */
    @Override
    @Transactional(readOnly = true)
    public List<FunctionOutDTO> obtenerFunctionsPorCode(String code) {
        return functionRepository.findByCodeContainingIgnoreCase(code).stream()
                .map(this::convertToOutDTO)
                .toList();
    }
    /**
     * Obtener todas las Functions por categoría.
     * @param category Categoría de la Function.
     * @return Lista de Functions en la categoría especificada.
     */
    @Override
    @Transactional(readOnly = true)
    public List<FunctionOutDTO> obtenerFunctionsPorCategory(Category category) {
        return functionRepository.findByCategory(category).stream()
                .map(this::convertToOutDTO)
                .toList();
    }
    /**
     * Obtener una Function por ID.
     * @param id ID de la Function.
     * @return Function encontrada si existe.
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<FunctionOutDTO> obtenerFunctionPorId(Long id) {
        return functionRepository.findById(id)
                .map(this::convertToOutDTO);
    }
    /**
     * Actualizar una Function existente.
     * @param id ID de la Function a actualizar.
     * @param functionUpdateDTO Datos de actualización de la Function.
     * @return Function actualizada si existe.
     */
    @Override
    @Transactional
    public Optional<FunctionOutDTO> actualizarFunction(Long id, FunctionUpdateDTO functionUpdateDTO) {
        return functionRepository.findById(id).map(function -> {
            if (functionUpdateDTO.getCode() != null) {
                // Verificar que el nuevo código no exista
                Optional<Function> existing = functionRepository.findByCode(functionUpdateDTO.getCode());
                if (existing.isPresent() && !existing.get().getId().equals(id)) {
                    throw new ResponseStatusException(HttpStatus.CONFLICT,
                            "Function code already exists");
                }
                function.setCode(functionUpdateDTO.getCode());
            }
            if (functionUpdateDTO.getName() != null) {
                function.setName(functionUpdateDTO.getName());
            }
            if (functionUpdateDTO.getCategory() != null) {
                function.setCategory(functionUpdateDTO.getCategory());
            }
            if (functionUpdateDTO.getDescription() != null) {
                function.setDescription(functionUpdateDTO.getDescription());
            }
            Function updatedFunction = functionRepository.save(function);
            return convertToOutDTO(updatedFunction);
        });
    }
    /**
     * Eliminar una Function por ID.
     * @param id ID de la Function a eliminar.
     * @return true si la Function fue eliminada, false si no existe.
     */
    @Override
    @Transactional
    public boolean eliminarFunction(Long id) {
        if (functionRepository.existsById(id)) {
            functionRepository.deleteById(id);
            return true;
        }
        return false;
    }
    /**
     * Convierte una entidad Function a un DTO de salida FunctionOutDTO.
     * @param function Entidad Function.
     * @return DTO de salida FunctionOutDTO.
     */
    private FunctionOutDTO convertToOutDTO(Function function) {
        FunctionOutDTO dto = new FunctionOutDTO();
        dto.setId(function.getId());
        dto.setCode(function.getCode());
        dto.setName(function.getName());
        dto.setCategory(function.getCategory());
        dto.setDescription(function.getDescription());
        dto.setCreatedAt(function.getCreatedAt());
        return dto;
    }
}