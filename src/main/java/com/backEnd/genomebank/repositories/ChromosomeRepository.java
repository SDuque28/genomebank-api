package com.backEnd.genomebank.repositories;

import com.backEnd.genomebank.entities.Chromosome;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChromosomeRepository extends JpaRepository<Chromosome, Long> {
    List<Chromosome> findByGenomeId(Long genomeId);
}