package com.example.gamelibrary.service;

import com.example.gamelibrary.model.dto.request.GameCompositeRequest;
import com.example.gamelibrary.model.dto.request.GameRequest;
import com.example.gamelibrary.model.dto.response.GameResponse;
import com.example.gamelibrary.model.dto.response.GameWithAchievementsResponse;
import com.example.gamelibrary.model.dto.response.GameWithReviewsResponse;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface GameService {
    List<GameResponse> findAll();

    List<GameResponse> findByGenre(String genre);

    List<GameResponse> findByMinRating(Integer minRating);

    List<GameResponse> findByDeveloper(String developer);

    List<GameWithReviewsResponse> findAllWithReviews();

    List<GameWithReviewsResponse> findAllWithReviewsNaive();

    List<GameWithAchievementsResponse> findAllWithAchievements();

    List<GameResponse> findTopRated(Integer limit);

    List<GameResponse> findNewReleases(Integer days);

    List<GameResponse> searchByKeyword(String keyword);

    Page<GameResponse> findByAchievementsWithJpql(
            String achievementName,
            String achievementDescription,
            Integer minRating,
            Pageable pageable
    );

    Page<GameResponse> findByAchievementsWithNative(
            String achievementName,
            String achievementDescription,
            Integer minRating,
            Pageable pageable
    );

    GameResponse findById(Long id);

    GameResponse create(GameRequest request);

    GameResponse createGameWithReviewAndAchievementNoTx(GameCompositeRequest request);

    GameResponse createGameWithReviewAndAchievementTx(GameCompositeRequest request);

    GameResponse update(Long id, GameRequest request);

    void delete(Long id);
}
