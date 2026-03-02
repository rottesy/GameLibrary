package com.example.gamelibrary.repository;

import com.example.gamelibrary.model.entity.Developer;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeveloperRepository extends JpaRepository<Developer, Long> {
    List<Developer> findByNameContainingIgnoreCase(String name);
}
