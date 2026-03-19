package com.example.gamelibrary.service;

import com.example.gamelibrary.model.dto.request.UserRequest;
import com.example.gamelibrary.model.dto.response.CollectionResponse;
import com.example.gamelibrary.model.dto.response.GameResponse;
import com.example.gamelibrary.model.dto.response.ReviewResponse;
import com.example.gamelibrary.model.dto.response.UserResponse;
import com.example.gamelibrary.model.dto.response.UserSummaryResponse;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {
    Page<UserSummaryResponse> findAll(Pageable pageable);

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
