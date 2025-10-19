package com.backEnd.genomebank.repositories;

import com.backEnd.genomebank.entities.Gene;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GeneRepository extends JpaRepository<Gene, Long> {

    List<Gene> findByChromosomeId(Long chromosomeId);

    List<Gene> findBySymbolContainingIgnoreCase(String symbol);

    /**
     * Encuentra genes que se solapan con un rango específico en un cromosoma.
     * Un gen se solapa si su rango [startPosition, endPosition] tiene intersección
     * con el rango [start, end].
     */
    @Query("SELECT g FROM Gene g WHERE g.chromosome.id = :chromosomeId " +
            "AND g.startPosition < :end AND g.endPosition > :start")
    List<Gene> findGenesInRange(
            @Param("chromosomeId") Long chromosomeId,
            @Param("start") Integer start,
            @Param("end") Integer end
    );
}