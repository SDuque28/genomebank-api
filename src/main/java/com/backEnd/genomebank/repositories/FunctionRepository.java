package com.backEnd.genomebank.repositories;

import com.backEnd.genomebank.entities.Function;
import com.backEnd.genomebank.entities.Function.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FunctionRepository extends JpaRepository<Function, Long> {

    Optional<Function> findByCode(String code);

    List<Function> findByCodeContainingIgnoreCase(String code);

    List<Function> findByCategory(Category category);
}