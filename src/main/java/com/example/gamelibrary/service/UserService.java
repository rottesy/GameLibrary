package com.example.gamelibrary.service;

import com.example.gamelibrary.model.dto.request.UserRequest;
import com.example.gamelibrary.model.dto.response.CollectionResponse;
import com.example.gamelibrary.model.dto.response.GameResponse;
import com.example.gamelibrary.model.dto.response.ReviewResponse;
import com.example.gamelibrary.model.dto.response.UserResponse;
import java.util.List;

public interface UserService {
    List<UserResponse> findAll();

    UserResponse findById(Long id);

    List<GameResponse> findUserGames(Long id);

    List<ReviewResponse> findUserReviews(Long id);

    List<CollectionResponse> findUserCollections(Long id);

    List<GameResponse> findUserWishlist(Long id);

    UserResponse addGameToLibrary(Long userId, Long gameId);

    UserResponse addGameToWishlist(Long userId, Long gameId);

    UserResponse create(UserRequest request);

    UserResponse update(Long id, UserRequest request);

    UserResponse removeGameFromLibrary(Long userId, Long gameId);

    UserResponse removeGameFromWishlist(Long userId, Long gameId);

    void delete(Long id);
}
