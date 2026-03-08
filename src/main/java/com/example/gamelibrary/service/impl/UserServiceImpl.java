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
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@AllArgsConstructor
public class UserServiceImpl implements UserService {
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
                .orElseThrow(() -> new UserNotFoundException("User not found: " + id));
        return userMapper.toResponse(user);
    }

    @Override
    public List<GameResponse> findUserGames(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + id));
        return user.getLibraryGames().stream()
                .map(gameMapper::toResponse)
                .toList();
    }

    @Override
    public List<ReviewResponse> findUserReviews(Long id) {
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException("User not found: " + id);
        }
        return reviewRepository.findByUserId(id).stream()
                .map(reviewMapper::toResponse)
                .toList();
    }

    @Override
    public List<CollectionResponse> findUserCollections(Long id) {
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException("User not found: " + id);
        }
        return collectionRepository.findByOwnerId(id).stream()
                .map(collectionMapper::toResponse)
                .toList();
    }

    @Override
    public List<GameResponse> findUserWishlist(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + id));
        return user.getWishlistGames().stream()
                .map(gameMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public UserResponse addGameToLibrary(Long userId, Long gameId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + userId));
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new GameNotFoundException("Game not found: " + gameId));
        user.getLibraryGames().add(game);
        User saved = userRepository.save(user);
        return userMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public UserResponse addGameToWishlist(Long userId, Long gameId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + userId));
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new GameNotFoundException("Game not found: " + gameId));
        user.getWishlistGames().add(game);
        User saved = userRepository.save(user);
        return userMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public UserResponse create(UserRequest request) {
        User user = userMapper.fromRequest(request);
        User saved = userRepository.save(user);
        return userMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public UserResponse update(Long id, UserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + id));
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        User saved = userRepository.save(user);
        return userMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public UserResponse removeGameFromLibrary(Long userId, Long gameId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + userId));
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new GameNotFoundException("Game not found: " + gameId));
        user.getLibraryGames().remove(game);
        User saved = userRepository.save(user);
        return userMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public UserResponse removeGameFromWishlist(Long userId, Long gameId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + userId));
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new GameNotFoundException("Game not found: " + gameId));
        user.getWishlistGames().remove(game);
        User saved = userRepository.save(user);
        return userMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException("User not found: " + id);
        }
        userRepository.deleteById(id);
    }
}
