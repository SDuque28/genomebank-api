package com.backEnd.genomebank.repositories;

import com.backEnd.genomebank.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

}