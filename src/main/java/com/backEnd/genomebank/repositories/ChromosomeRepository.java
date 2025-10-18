package com.backEnd.genomebank.repositories;

import com.backEnd.genomebank.entities.Chromosome;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChromosomeRepository extends JpaRepository<Chromosome, Long> {
}
