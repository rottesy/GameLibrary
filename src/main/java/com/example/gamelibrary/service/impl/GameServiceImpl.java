package com.example.gamelibrary.service.impl;

import com.example.gamelibrary.exception.DeveloperNotFoundException;
import com.example.gamelibrary.exception.GameNotFoundException;
import com.example.gamelibrary.exception.GenreNotFoundException;
import com.example.gamelibrary.exception.UserNotFoundException;
import com.example.gamelibrary.mapper.AchievementMapper;
import com.example.gamelibrary.mapper.GameMapper;
import com.example.gamelibrary.mapper.ReviewMapper;
import com.example.gamelibrary.model.dto.request.GameCompositeRequest;
import com.example.gamelibrary.model.dto.request.GameRequest;
import com.example.gamelibrary.model.dto.response.AchievementResponse;
import com.example.gamelibrary.model.dto.response.GameResponse;
import com.example.gamelibrary.model.dto.response.GameWithAchievementsResponse;
import com.example.gamelibrary.model.dto.response.GameWithReviewsResponse;
import com.example.gamelibrary.model.dto.response.ReviewResponse;
import com.example.gamelibrary.model.entity.Achievement;
import com.example.gamelibrary.model.entity.Developer;
import com.example.gamelibrary.model.entity.Game;
import com.example.gamelibrary.model.entity.Genre;
import com.example.gamelibrary.model.entity.Review;
import com.example.gamelibrary.model.entity.User;
import com.example.gamelibrary.repository.AchievementRepository;
import com.example.gamelibrary.repository.DeveloperRepository;
import com.example.gamelibrary.repository.GameRepository;
import com.example.gamelibrary.repository.GenreRepository;
import com.example.gamelibrary.repository.ReviewRepository;
import com.example.gamelibrary.repository.UserRepository;
import com.example.gamelibrary.service.GameService;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@AllArgsConstructor
public class GameServiceImpl implements GameService {
    private final GameRepository gameRepository;
    private final DeveloperRepository developerRepository;
    private final GenreRepository genreRepository;
    private final ReviewRepository reviewRepository;
    private final AchievementRepository achievementRepository;
    private final UserRepository userRepository;
    private final GameMapper gameMapper;
    private final ReviewMapper reviewMapper;
    private final AchievementMapper achievementMapper;

    @Override
    public List<GameResponse> findAll() {
        return gameRepository.findAll().stream()
                .map(gameMapper::toResponse)
                .toList();
    }

    @Override
    public List<GameResponse> findByGenre(String genre) {
        return gameRepository.findByGenresNameIgnoreCase(genre).stream()
                .map(gameMapper::toResponse)
                .toList();
    }

    @Override
    public List<GameResponse> findByMinRating(Integer minRating) {
        return gameRepository.findByRatingGreaterThanEqual(minRating).stream()
                .map(gameMapper::toResponse)
                .toList();
    }

    @Override
    public List<GameResponse> findByDeveloper(String developer) {
        return gameRepository.findByDeveloperNameContainingIgnoreCase(developer).stream()
                .map(gameMapper::toResponse)
                .toList();
    }

    @Override
    public List<GameWithReviewsResponse> findAllWithReviews() {
        return gameRepository.findAllWithReviews().stream()
                .map(this::toGameWithReviewsResponse)
                .toList();
    }

    @Override
    public List<GameWithReviewsResponse> findAllWithReviewsNaive() {
        return gameRepository.findAll().stream()
                .map(this::toGameWithReviewsResponse)
                .toList();
    }

    @Override
    public List<GameWithAchievementsResponse> findAllWithAchievements() {
        return gameRepository.findAllWithAchievements().stream()
                .map(this::toGameWithAchievementsResponse)
                .toList();
    }

    @Override
    public List<GameResponse> findTopRated(Integer limit) {
        return gameRepository.findAllByOrderByRatingDesc(PageRequest.of(0, limit)).stream()
                .map(gameMapper::toResponse)
                .toList();
    }

    @Override
    public List<GameResponse> findNewReleases(Integer days) {
        LocalDate fromDate = LocalDate.now().minusDays(days);
        return gameRepository.findByReleaseDateAfter(fromDate).stream()
                .map(gameMapper::toResponse)
                .toList();
    }

    @Override
    public List<GameResponse> searchByKeyword(String keyword) {
        return gameRepository
                .findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(keyword, keyword).stream()
                .map(gameMapper::toResponse)
                .toList();
    }

    @Override
    public GameResponse findById(Long id) {
        Game game = gameRepository.findById(id)
                .orElseThrow(() -> new GameNotFoundException("Game not found: " + id));
        return gameMapper.toResponse(game);
    }

    @Override
    @Transactional
    public GameResponse create(GameRequest request) {
        Game game = gameMapper.fromRequest(request);
        game.setDeveloper(resolveDeveloper(request.getDeveloperId()));
        game.setGenres(resolveGenres(request.getGenreIds()));
        Game saved = gameRepository.save(game);
        return gameMapper.toResponse(saved);
    }

    @Override
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public GameResponse createGameWithReviewAndAchievementNoTx(GameCompositeRequest request) {
        return createGameWithReviewAndAchievementInternal(request);
    }

    @Override
    @Transactional
    public GameResponse createGameWithReviewAndAchievementTx(GameCompositeRequest request) {
        return createGameWithReviewAndAchievementInternal(request);
    }

    @Override
    @Transactional
    public GameResponse update(Long id, GameRequest request) {
        Game game = gameRepository.findById(id)
                .orElseThrow(() -> new GameNotFoundException("Game not found: " + id));
        game.setTitle(request.getTitle());
        game.setDescription(request.getDescription());
        game.setReleaseDate(request.getReleaseDate());
        game.setRating(request.getRating());
        game.setDeveloper(resolveDeveloper(request.getDeveloperId()));
        game.setGenres(resolveGenres(request.getGenreIds()));
        Game saved = gameRepository.save(game);
        return gameMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!gameRepository.existsById(id)) {
            throw new GameNotFoundException("Game not found: " + id);
        }
        gameRepository.deleteUserGamesByGameId(id);
        gameRepository.deleteUserWishlistByGameId(id);
        gameRepository.deleteCollectionGamesByGameId(id);
        gameRepository.deleteGameGenresByGameId(id);
        gameRepository.deleteById(id);
    }

    private Developer resolveDeveloper(Long developerId) {
        return developerRepository.findById(developerId)
                .orElseThrow(() -> new DeveloperNotFoundException("Developer not found: " + developerId));
    }

    private User resolveUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + userId));
    }

    private Set<Genre> resolveGenres(Set<Long> genreIds) {
        if (genreIds == null || genreIds.isEmpty()) {
            return new HashSet<>();
        }
        Set<Genre> genres = new HashSet<>();
        for (Long genreId : genreIds) {
            Genre genre = genreRepository.findById(genreId)
                    .orElseThrow(() -> new GenreNotFoundException("Genre not found: " + genreId));
            genres.add(genre);
        }
        return genres;
    }

    private GameResponse createGameWithReviewAndAchievementInternal(GameCompositeRequest request) {
        Game game = gameMapper.fromRequest(request.getGame());
        game.setDeveloper(resolveDeveloper(request.getGame().getDeveloperId()));
        game.setGenres(resolveGenres(request.getGame().getGenreIds()));
        Game savedGame = gameRepository.save(game);

        Review review = reviewMapper.fromRequest(request.getReview());
        review.setGame(savedGame);
        review.setUser(resolveUser(request.getReview().getUserId()));
        review.setCreatedAt(LocalDateTime.now());
        reviewRepository.save(review);

        if (shouldFailComposite(request)) {
            throw new IllegalStateException("Simulated failure after review save");
        }

        Achievement achievement = achievementMapper.fromRequest(request.getAchievement());
        achievement.setGame(savedGame);
        achievementRepository.save(achievement);

        return gameMapper.toResponse(savedGame);
    }

    private boolean shouldFailComposite(GameCompositeRequest request) {
        String name = request.getAchievement().getName();
        return name != null && name.equalsIgnoreCase("FAIL");
    }

    private GameWithReviewsResponse toGameWithReviewsResponse(Game game) {
        List<ReviewResponse> reviews = game.getReviews().stream()
                .map(reviewMapper::toResponse)
                .toList();
        return new GameWithReviewsResponse(gameMapper.toResponse(game), reviews);
    }

    private GameWithAchievementsResponse toGameWithAchievementsResponse(Game game) {
        List<AchievementResponse> achievements = game.getAchievements().stream()
                .map(achievementMapper::toResponse)
                .toList();
        return new GameWithAchievementsResponse(gameMapper.toResponse(game), achievements);
    }
}
