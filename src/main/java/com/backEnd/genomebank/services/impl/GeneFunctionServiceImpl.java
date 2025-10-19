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
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GeneFunctionServiceImpl implements IGeneFunctionService {

    private final GeneFunctionRepository geneFunctionRepository;
    private final GeneRepository geneRepository;
    private final FunctionRepository functionRepository;

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

    @Override
    @Transactional(readOnly = true)
    public List<GeneFunctionOutDTO> obtenerFunctionsPorGene(Long geneId) {
        return geneFunctionRepository.findByGeneId(geneId).stream()
                .map(this::convertToOutDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<GeneFunctionOutDTO> obtenerGenesPorFunction(Long functionId) {
        return geneFunctionRepository.findByFunctionId(functionId).stream()
                .map(this::convertToOutDTO)
                .collect(Collectors.toList());
    }

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