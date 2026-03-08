package com.example.gamelibrary.service.impl;

import com.example.gamelibrary.exception.CollectionNotFoundException;
import com.example.gamelibrary.exception.GameNotFoundException;
import com.example.gamelibrary.exception.UserNotFoundException;
import com.example.gamelibrary.mapper.CollectionMapper;
import com.example.gamelibrary.mapper.GameMapper;
import com.example.gamelibrary.model.dto.request.CollectionRequest;
import com.example.gamelibrary.model.dto.response.CollectionResponse;
import com.example.gamelibrary.model.dto.response.GameResponse;
import com.example.gamelibrary.model.entity.Collection;
import com.example.gamelibrary.model.entity.Game;
import com.example.gamelibrary.model.entity.User;
import com.example.gamelibrary.repository.CollectionRepository;
import com.example.gamelibrary.repository.GameRepository;
import com.example.gamelibrary.repository.UserRepository;
import com.example.gamelibrary.service.CollectionService;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@AllArgsConstructor
public class CollectionServiceImpl implements CollectionService {
    private static final String COLLECTION_NOT_FOUND = "Collection not found: ";
    private static final String USER_NOT_FOUND = "User not found: ";
    private static final String GAME_NOT_FOUND = "Game not found: ";

    private final CollectionRepository collectionRepository;
    private final UserRepository userRepository;
    private final GameRepository gameRepository;
    private final CollectionMapper collectionMapper;
    private final GameMapper gameMapper;

    @Override
    public List<CollectionResponse> findAll() {
        return collectionRepository.findAll().stream()
                .map(collectionMapper::toResponse)
                .toList();
    }

    @Override
    public CollectionResponse findById(Long id) {
        Collection collection = collectionRepository.findById(id)
                .orElseThrow(() -> new CollectionNotFoundException(COLLECTION_NOT_FOUND + id));
        return collectionMapper.toResponse(collection);
    }

    @Override
    public List<CollectionResponse> findByUserId(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException(USER_NOT_FOUND + userId);
        }
        return collectionRepository.findByOwnerId(userId).stream()
                .map(collectionMapper::toResponse)
                .toList();
    }

    @Override
    public List<GameResponse> findGamesInCollection(Long id) {
        Collection collection = collectionRepository.findById(id)
                .orElseThrow(() -> new CollectionNotFoundException(COLLECTION_NOT_FOUND + id));
        return collection.getGames().stream()
                .map(gameMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public CollectionResponse create(CollectionRequest request) {
        Collection collection = collectionMapper.fromRequest(request);
        collection.setOwner(resolveUser(request.getOwnerId()));
        collection.setGames(resolveGames(request.getGameIds()));
        Collection saved = collectionRepository.save(collection);
        return collectionMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public CollectionResponse addGameToCollection(Long collectionId, Long gameId) {
        Collection collection = collectionRepository.findById(collectionId)
                .orElseThrow(() -> new CollectionNotFoundException(COLLECTION_NOT_FOUND + collectionId));
        Game game = resolveGame(gameId);
        collection.getGames().add(game);
        Collection saved = collectionRepository.save(collection);
        return collectionMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public CollectionResponse update(Long id, CollectionRequest request) {
        Collection collection = collectionRepository.findById(id)
                .orElseThrow(() -> new CollectionNotFoundException(COLLECTION_NOT_FOUND + id));
        collection.setName(request.getName());
        collection.setOwner(resolveUser(request.getOwnerId()));
        if (request.getGameIds() != null) {
            collection.setGames(resolveGames(request.getGameIds()));
        }
        Collection saved = collectionRepository.save(collection);
        return collectionMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public CollectionResponse removeGameFromCollection(Long collectionId, Long gameId) {
        Collection collection = collectionRepository.findById(collectionId)
                .orElseThrow(() -> new CollectionNotFoundException(COLLECTION_NOT_FOUND + collectionId));
        Game game = resolveGame(gameId);
        collection.getGames().remove(game);
        Collection saved = collectionRepository.save(collection);
        return collectionMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!collectionRepository.existsById(id)) {
            throw new CollectionNotFoundException(COLLECTION_NOT_FOUND + id);
        }
        collectionRepository.deleteById(id);
    }

    private User resolveUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND + userId));
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
