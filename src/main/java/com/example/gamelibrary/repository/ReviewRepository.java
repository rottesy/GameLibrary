package com.example.gamelibrary.repository;

import com.example.gamelibrary.model.entity.Review;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByGame_Id(Long gameId);

    List<Review> findByUser_Id(Long userId);
}
