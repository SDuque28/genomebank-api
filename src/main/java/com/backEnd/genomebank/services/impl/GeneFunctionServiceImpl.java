package com.backEnd.genomebank.services.impl;

import com.backEnd.genomebank.dto.genefunction.GeneFunctionOutDTO;
import com.backEnd.genomebank.entities.Gene;
import com.backEnd.genomebank.entities.GeneFunction;
import com.backEnd.genomebank.entities.GeneFunctionId;
import com.backEnd.genomebank.entities.Function;
import com.backEnd.genomebank.repositories.GeneFunctionRepository;
import com.backEnd.genomebank.repositories.GeneRepository;
import com.backEnd.genomebank.repositories.FunctionRepository;
import com.backEnd.genomebank.services.IGeneFunctionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GeneFunctionServiceImpl implements IGeneFunctionService {

    private final GeneFunctionRepository geneFunctionRepository;
    private final GeneRepository geneRepository;
    private final FunctionRepository functionRepository;
    /**
     * Asocia una Function a un Gene con evidencia opcional.
     * @param geneId ID del Gene.
     * @param functionId ID de la Function.
     * @param evidence Evidencia de la asociación (opcional).
     * @return GeneFunctionOutDTO de la asociación creada.
     */
    @Override
    @Transactional
    public GeneFunctionOutDTO asociarFunctionAGene(Long geneId, Long functionId, String evidence) {
        Gene gene = geneRepository.findById(geneId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Gene not found"));

        Function function = functionRepository.findById(functionId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Function not found"));

        // Verificar si ya existe la asociación
        GeneFunctionId id = new GeneFunctionId();
        id.setGeneId(geneId);
        id.setFunctionId(functionId);

        if (geneFunctionRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Association already exists");
        }

        GeneFunction geneFunction = new GeneFunction();
        geneFunction.setId(id);
        geneFunction.setGene(gene);
        geneFunction.setFunction(function);
        geneFunction.setEvidence(evidence);

        GeneFunction savedGeneFunction = geneFunctionRepository.save(geneFunction);
        return convertToOutDTO(savedGeneFunction);
    }
    /**
     * Obtiene todas las Functions asociadas a un Gene específico.
     * @param geneId ID del Gene.
     * @return Lista de GeneFunctionOutDTO asociados al Gene.
     */
    @Override
    @Transactional(readOnly = true)
    public List<GeneFunctionOutDTO> obtenerFunctionsPorGene(Long geneId) {
        return geneFunctionRepository.findByGeneId(geneId).stream()
                .map(this::convertToOutDTO)
                .toList();
    }
    /**
     * Obtiene todos los Genes asociados a una Function específica.
     * @param functionId ID de la Function.
     * @return Lista de GeneFunctionOutDTO asociados a la Function.
     */
    @Override
    @Transactional(readOnly = true)
    public List<GeneFunctionOutDTO> obtenerGenesPorFunction(Long functionId) {
        return geneFunctionRepository.findByFunctionId(functionId).stream()
                .map(this::convertToOutDTO)
                .toList();
    }
    /**
     * Elimina la asociación entre un Gene y una Function.
     * @param geneId ID del Gene.
     * @param functionId ID de la Function.
     * @return true si la asociación fue eliminada, false si no existía.
     */
    @Override
    @Transactional
    public boolean eliminarAsociacion(Long geneId, Long functionId) {
        GeneFunctionId id = new GeneFunctionId();
        id.setGeneId(geneId);
        id.setFunctionId(functionId);

        if (geneFunctionRepository.existsById(id)) {
            geneFunctionRepository.deleteById(id);
            return true;
        }
        return false;
    }
    /**
     * Convierte una entidad GeneFunction a un DTO de salida GeneFunctionOutDTO.
     * @param geneFunction La entidad GeneFunction a convertir.
     * @return El DTO de salida correspondiente.
     */
    private GeneFunctionOutDTO convertToOutDTO(GeneFunction geneFunction) {
        GeneFunctionOutDTO dto = new GeneFunctionOutDTO();
        dto.setGeneId(geneFunction.getGene().getId());
        dto.setGeneSymbol(geneFunction.getGene().getSymbol());
        dto.setFunctionId(geneFunction.getFunction().getId());
        dto.setFunctionCode(geneFunction.getFunction().getCode());
        dto.setFunctionName(geneFunction.getFunction().getName());
        dto.setEvidence(geneFunction.getEvidence());
        dto.setCreatedAt(geneFunction.getCreatedAt());
        return dto;
    }
}