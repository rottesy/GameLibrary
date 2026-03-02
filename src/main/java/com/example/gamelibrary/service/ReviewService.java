package com.example.gamelibrary.service;

import com.example.gamelibrary.model.dto.request.ReviewRequest;
import com.example.gamelibrary.model.dto.response.ReviewResponse;
import java.util.List;

public interface ReviewService {
    List<ReviewResponse> findAll();

    ReviewResponse findById(Long id);

    List<ReviewResponse> findByGameId(Long gameId);

    List<ReviewResponse> findByUserId(Long userId);

    ReviewResponse create(ReviewRequest request);

    ReviewResponse update(Long id, ReviewRequest request);

    void delete(Long id);
}
