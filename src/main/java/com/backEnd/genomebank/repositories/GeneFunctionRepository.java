package com.backEnd.genomebank.repositories;

import com.backEnd.genomebank.entities.GeneFunction;
import com.backEnd.genomebank.entities.GeneFunctionId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GeneFunctionRepository extends JpaRepository<GeneFunction, GeneFunctionId> {
}
