package com.backEnd.genomebank.repositories;

import com.backEnd.genomebank.entities.Genome;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GenomeRepository extends JpaRepository<Genome, Long> {
    List<Genome> findBySpeciesId(Long speciesId);
}