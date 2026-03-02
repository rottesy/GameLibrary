package com.example.gamelibrary.service.impl;

import com.example.gamelibrary.exception.GameNotFoundException;
import com.example.gamelibrary.exception.ReviewNotFoundException;
import com.example.gamelibrary.exception.UserNotFoundException;
import com.example.gamelibrary.mapper.ReviewMapper;
import com.example.gamelibrary.model.dto.request.ReviewRequest;
import com.example.gamelibrary.model.dto.response.ReviewResponse;
import com.example.gamelibrary.model.entity.Game;
import com.example.gamelibrary.model.entity.Review;
import com.example.gamelibrary.model.entity.User;
import com.example.gamelibrary.repository.GameRepository;
import com.example.gamelibrary.repository.ReviewRepository;
import com.example.gamelibrary.repository.UserRepository;
import com.example.gamelibrary.service.ReviewService;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@AllArgsConstructor
public class ReviewServiceImpl implements ReviewService {
    private final ReviewRepository reviewRepository;
    private final GameRepository gameRepository;
    private final UserRepository userRepository;
    private final ReviewMapper reviewMapper;

    @Override
    public List<ReviewResponse> findAll() {
        return reviewRepository.findAll().stream()
                .map(reviewMapper::toResponse)
                .toList();
    }

    @Override
    public ReviewResponse findById(Long id) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new ReviewNotFoundException("Review not found: " + id));
        return reviewMapper.toResponse(review);
    }

    @Override
    public List<ReviewResponse> findByGameId(Long gameId) {
        if (!gameRepository.existsById(gameId)) {
            throw new GameNotFoundException("Game not found: " + gameId);
        }
        return reviewRepository.findByGame_Id(gameId).stream()
                .map(reviewMapper::toResponse)
                .toList();
    }

    @Override
    public List<ReviewResponse> findByUserId(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException("User not found: " + userId);
        }
        return reviewRepository.findByUser_Id(userId).stream()
                .map(reviewMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public ReviewResponse create(ReviewRequest request) {
        Review review = reviewMapper.fromRequest(request);
        review.setGame(resolveGame(request.getGameId()));
        review.setUser(resolveUser(request.getUserId()));
        review.setCreatedAt(LocalDateTime.now());
        Review saved = reviewRepository.save(review);
        return reviewMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public ReviewResponse update(Long id, ReviewRequest request) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new ReviewNotFoundException("Review not found: " + id));
        review.setRating(request.getRating());
        review.setComment(request.getComment());
        review.setGame(resolveGame(request.getGameId()));
        review.setUser(resolveUser(request.getUserId()));
        Review saved = reviewRepository.save(review);
        return reviewMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!reviewRepository.existsById(id)) {
            throw new ReviewNotFoundException("Review not found: " + id);
        }
        reviewRepository.deleteById(id);
    }

    private Game resolveGame(Long gameId) {
        return gameRepository.findById(gameId)
                .orElseThrow(() -> new GameNotFoundException("Game not found: " + gameId));
    }

    private User resolveUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + userId));
    }
}
