package com.example.gamelibrary.repository;

import com.example.gamelibrary.model.entity.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CollectionRepository extends JpaRepository<Collection, Long> {
    List<Collection> findByOwner_Id(Long ownerId);
}
