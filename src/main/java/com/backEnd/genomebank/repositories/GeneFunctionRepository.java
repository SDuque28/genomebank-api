package com.backEnd.genomebank.repositories;

import com.backEnd.genomebank.entities.GeneFunction;
import com.backEnd.genomebank.entities.GeneFunctionId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GeneFunctionRepository extends JpaRepository<GeneFunction, GeneFunctionId> {

    @Query("SELECT gf FROM GeneFunction gf WHERE gf.gene.id = :geneId")
    List<GeneFunction> findByGeneId(@Param("geneId") Long geneId);

    @Query("SELECT gf FROM GeneFunction gf WHERE gf.function.id = :functionId")
    List<GeneFunction> findByFunctionId(@Param("functionId") Long functionId);
}