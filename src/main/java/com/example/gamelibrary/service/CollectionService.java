package com.example.gamelibrary.service;

import com.example.gamelibrary.model.dto.request.CollectionRequest;
import com.example.gamelibrary.model.dto.response.CollectionResponse;
import com.example.gamelibrary.model.dto.response.GameResponse;
import java.util.List;

public interface CollectionService {
    List<CollectionResponse> findAll();

    CollectionResponse findById(Long id);

    List<CollectionResponse> findByUserId(Long userId);

    List<GameResponse> findGamesInCollection(Long id);

    CollectionResponse create(CollectionRequest request);

    CollectionResponse addGameToCollection(Long collectionId, Long gameId);

    CollectionResponse update(Long id, CollectionRequest request);

    CollectionResponse removeGameFromCollection(Long collectionId, Long gameId);

    void delete(Long id);
}
