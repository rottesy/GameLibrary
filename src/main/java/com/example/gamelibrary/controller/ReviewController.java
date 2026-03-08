package com.example.gamelibrary.controller;

import com.example.gamelibrary.model.dto.request.ReviewRequest;
import com.example.gamelibrary.model.dto.response.ReviewResponse;
import com.example.gamelibrary.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/api/reviews")
@Tag(name = "Reviews", description = "Review CRUD operations for games")
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping
    @Operation(summary = "Get all reviews")
    @ApiResponse(responseCode = "200", description = "Reviews returned")
    public ResponseEntity<List<ReviewResponse>> getAll() {
        return ResponseEntity.ok(reviewService.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get review by id")
    @ApiResponse(responseCode = "200", description = "Review found")
    @ApiResponse(responseCode = "404", description = "Review not found")
    public ResponseEntity<ReviewResponse> getById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(reviewService.findById(id));
    }

    @GetMapping("/game/{gameId}")
    @Operation(summary = "Get all reviews for a specific game")
    @ApiResponse(responseCode = "200", description = "Reviews returned")
    public ResponseEntity<List<ReviewResponse>> getByGameId(@PathVariable("gameId") Long gameId) {
        return ResponseEntity.ok(reviewService.findByGameId(gameId));
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get all reviews by a specific user")
    @ApiResponse(responseCode = "200", description = "Reviews returned")
    public ResponseEntity<List<ReviewResponse>> getByUserId(@PathVariable("userId") Long userId) {
        return ResponseEntity.ok(reviewService.findByUserId(userId));
    }

    @PostMapping
    @Operation(summary = "Create review")
    @ApiResponse(responseCode = "201", description = "Review created")
    @ApiResponse(responseCode = "400", description = "Validation error")
    public ResponseEntity<ReviewResponse> create(@Valid @RequestBody ReviewRequest request) {
        ReviewResponse response = reviewService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update review")
    @ApiResponse(responseCode = "200", description = "Review updated")
    @ApiResponse(responseCode = "400", description = "Validation error")
    @ApiResponse(responseCode = "404", description = "Review not found")
    public ResponseEntity<ReviewResponse> update(
            @PathVariable("id") Long id,
            @Valid @RequestBody ReviewRequest request
    ) {
        return ResponseEntity.ok(reviewService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete review")
    @ApiResponse(responseCode = "204", description = "Review deleted")
    @ApiResponse(responseCode = "404", description = "Review not found")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        reviewService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
