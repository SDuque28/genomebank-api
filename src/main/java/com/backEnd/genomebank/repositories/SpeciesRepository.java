package com.backEnd.genomebank.repositories;

import com.backEnd.genomebank.entities.Species;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpeciesRepository  extends JpaRepository<Species, Long> {
}
