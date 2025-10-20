package com.backEnd.genomebank.services;

import com.backEnd.genomebank.dto.function.*;
import com.backEnd.genomebank.entities.Function.Category;
import java.util.List;
import java.util.Optional;

public interface IFunctionService {
    FunctionOutDTO crearFunction(FunctionInDTO functionInDTO);
    List<FunctionOutDTO> obtenerTodasFunctions();
    List<FunctionOutDTO> obtenerFunctionsPorCode(String code);
    List<FunctionOutDTO> obtenerFunctionsPorCategory(Category category);
    Optional<FunctionOutDTO> obtenerFunctionPorId(Long id);
    Optional<FunctionOutDTO> actualizarFunction(Long id, FunctionUpdateDTO functionUpdateDTO);
    boolean eliminarFunction(Long id);
}