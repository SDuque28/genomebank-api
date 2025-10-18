package com.backEnd.genomebank.repositories;

import com.backEnd.genomebank.entities.Gene;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GeneRepository extends JpaRepository<Gene, Long> {
}
