package com.backEnd.genomebank.repositories;

import com.backEnd.genomebank.entities.Function;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FunctionRepository extends JpaRepository<Function,Long> {
}
