package com.example.gamelibrary.repository;

import com.example.gamelibrary.model.entity.Game;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GameRepository extends JpaRepository<Game, Long> {
    List<Game> findByGenres_NameIgnoreCase(String name);

    List<Game> findByGenres_Id(Long genreId);

    List<Game> findByRatingGreaterThanEqual(Integer rating);

    List<Game> findByDeveloper_NameContainingIgnoreCase(String developer);

    List<Game> findByDeveloper_Id(Long developerId);

    List<Game> findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String title, String description);

    List<Game> findByReleaseDateAfter(LocalDate date);

    Page<Game> findAllByOrderByRatingDesc(Pageable pageable);
}
