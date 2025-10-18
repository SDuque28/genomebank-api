package com.backEnd.genomebank.repositories;

import com.backEnd.genomebank.entities.Genome;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GenomeRepository extends JpaRepository<Genome,Long> {
}
