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
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FunctionServiceImpl implements IFunctionService {

    private final FunctionRepository functionRepository;

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

    @Override
    @Transactional(readOnly = true)
    public List<FunctionOutDTO> obtenerTodasFunctions() {
        return functionRepository.findAll().stream()
                .map(this::convertToOutDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<FunctionOutDTO> obtenerFunctionsPorCode(String code) {
        return functionRepository.findByCodeContainingIgnoreCase(code).stream()
                .map(this::convertToOutDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<FunctionOutDTO> obtenerFunctionsPorCategory(Category category) {
        return functionRepository.findByCategory(category).stream()
                .map(this::convertToOutDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<FunctionOutDTO> obtenerFunctionPorId(Long id) {
        return functionRepository.findById(id)
                .map(this::convertToOutDTO);
    }

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

    @Override
    @Transactional
    public boolean eliminarFunction(Long id) {
        if (functionRepository.existsById(id)) {
            functionRepository.deleteById(id);
            return true;
        }
        return false;
    }

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