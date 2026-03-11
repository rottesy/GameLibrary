package com.example.gamelibrary.service.impl;

import com.example.gamelibrary.exception.GameNotFoundException;
import com.example.gamelibrary.exception.UserNotFoundException;
import com.example.gamelibrary.mapper.CollectionMapper;
import com.example.gamelibrary.mapper.GameMapper;
import com.example.gamelibrary.mapper.ReviewMapper;
import com.example.gamelibrary.mapper.UserMapper;
import com.example.gamelibrary.model.dto.request.UserRequest;
import com.example.gamelibrary.model.dto.response.CollectionResponse;
import com.example.gamelibrary.model.dto.response.GameResponse;
import com.example.gamelibrary.model.dto.response.ReviewResponse;
import com.example.gamelibrary.model.dto.response.UserResponse;
import com.example.gamelibrary.model.entity.Game;
import com.example.gamelibrary.model.entity.User;
import com.example.gamelibrary.repository.CollectionRepository;
import com.example.gamelibrary.repository.GameRepository;
import com.example.gamelibrary.repository.ReviewRepository;
import com.example.gamelibrary.repository.UserRepository;
import com.example.gamelibrary.service.UserService;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    private static final String USER_NOT_FOUND = "User not found: ";
    private static final String GAME_NOT_FOUND = "Game not found: ";

    private final UserRepository userRepository;
    private final GameRepository gameRepository;
    private final ReviewRepository reviewRepository;
    private final CollectionRepository collectionRepository;
    private final UserMapper userMapper;
    private final GameMapper gameMapper;
    private final ReviewMapper reviewMapper;
    private final CollectionMapper collectionMapper;

    @Override
    public List<UserResponse> findAll() {
        return userRepository.findAll().stream()
                .map(userMapper::toResponse)
                .toList();
    }

    @Override
    public UserResponse findById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND + id));
        return userMapper.toResponse(user);
    }

    @Override
    public List<GameResponse> findUserGames(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND + id));
        return user.getLibraryGames().stream()
                .map(gameMapper::toResponse)
                .toList();
    }

    @Override
    public List<ReviewResponse> findUserReviews(Long id) {
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException(USER_NOT_FOUND + id);
        }
        return reviewRepository.findByUserId(id).stream()
                .map(reviewMapper::toResponse)
                .toList();
    }

    @Override
    public List<CollectionResponse> findUserCollections(Long id) {
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException(USER_NOT_FOUND + id);
        }
        return collectionRepository.findByOwnerId(id).stream()
                .map(collectionMapper::toResponse)
                .toList();
    }

    @Override
    public List<GameResponse> findUserWishlist(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND + id));
        return user.getWishlistGames().stream()
                .map(gameMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public UserResponse addGameToLibrary(Long userId, Long gameId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND + userId));
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new GameNotFoundException(GAME_NOT_FOUND + gameId));
        user.getLibraryGames().add(game);
        User saved = userRepository.save(user);
        return userMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public UserResponse addGameToWishlist(Long userId, Long gameId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND + userId));
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new GameNotFoundException(GAME_NOT_FOUND + gameId));
        user.getWishlistGames().add(game);
        User saved = userRepository.save(user);
        return userMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public UserResponse create(UserRequest request) {
        User user = userMapper.fromRequest(request);
        user.setLibraryGames(resolveGames(request.getLibraryGameIds()));
        user.setWishlistGames(resolveGames(request.getWishlistGameIds()));
        User saved = userRepository.save(user);
        return userMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public UserResponse update(Long id, UserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND + id));
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        User saved = userRepository.save(user);
        return userMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public UserResponse removeGameFromLibrary(Long userId, Long gameId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND + userId));
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new GameNotFoundException(GAME_NOT_FOUND + gameId));
        user.getLibraryGames().remove(game);
        User saved = userRepository.save(user);
        return userMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public UserResponse removeGameFromWishlist(Long userId, Long gameId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND + userId));
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new GameNotFoundException(GAME_NOT_FOUND + gameId));
        user.getWishlistGames().remove(game);
        User saved = userRepository.save(user);
        return userMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException(USER_NOT_FOUND + id);
        }
        userRepository.deleteById(id);
    }

    private Game resolveGame(Long gameId) {
        return gameRepository.findById(gameId)
                .orElseThrow(() -> new GameNotFoundException(GAME_NOT_FOUND + gameId));
    }

    private Set<Game> resolveGames(Set<Long> gameIds) {
        if (gameIds == null || gameIds.isEmpty()) {
            return new HashSet<>();
        }
        Set<Game> games = new HashSet<>();
        for (Long gameId : gameIds) {
            games.add(resolveGame(gameId));
        }
        return games;
    }
}
