package com.example.gamelibrary.controller;

import com.example.gamelibrary.dto.request.UserRequest;
import com.example.gamelibrary.dto.response.UserResponse;
import com.example.gamelibrary.dto.response.GameResponse;
import com.example.gamelibrary.dto.response.ReviewResponse;
import com.example.gamelibrary.dto.response.CollectionResponse;
import com.example.gamelibrary.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/api/users")
@Tag(name = "Users", description = "User CRUD operations and game library management")
public class UserController {

    private final UserService userService;

    @GetMapping
    @Operation(summary = "Get all users")
    @ApiResponse(responseCode = "200", description = "Users returned")
    public ResponseEntity<List<UserResponse>> getAll() {
        return ResponseEntity.ok(userService.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get user by id")
    @ApiResponse(responseCode = "200", description = "User found")
    @ApiResponse(responseCode = "404", description = "User not found")
    public ResponseEntity<UserResponse> getById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(userService.findById(id));
    }

    @GetMapping("/{id}/games")
    @Operation(summary = "Get user's game library")
    @ApiResponse(responseCode = "200", description = "Games returned")
    @ApiResponse(responseCode = "404", description = "User not found")
    public ResponseEntity<List<GameResponse>> getUserGames(@PathVariable("id") Long id) {
        return ResponseEntity.ok(userService.findUserGames(id));
    }

    @GetMapping("/{id}/reviews")
    @Operation(summary = "Get user's reviews")
    @ApiResponse(responseCode = "200", description = "Reviews returned")
    @ApiResponse(responseCode = "404", description = "User not found")
    public ResponseEntity<List<ReviewResponse>> getUserReviews(@PathVariable("id") Long id) {
        return ResponseEntity.ok(userService.findUserReviews(id));
    }

    @GetMapping("/{id}/collections")
    @Operation(summary = "Get user's game collections")
    @ApiResponse(responseCode = "200", description = "Collections returned")
    @ApiResponse(responseCode = "404", description = "User not found")
    public ResponseEntity<List<CollectionResponse>> getUserCollections(@PathVariable("id") Long id) {
        return ResponseEntity.ok(userService.findUserCollections(id));
    }

    @GetMapping("/{id}/wishlist")
    @Operation(summary = "Get user's wishlist")
    @ApiResponse(responseCode = "200", description = "Wishlist returned")
    @ApiResponse(responseCode = "404", description = "User not found")
    public ResponseEntity<List<GameResponse>> getUserWishlist(@PathVariable("id") Long id) {
        return ResponseEntity.ok(userService.findUserWishlist(id));
    }

    @PostMapping("/{userId}/games/{gameId}")
    @Operation(summary = "Add game to user's library")
    @ApiResponse(responseCode = "200", description = "Game added to library")
    @ApiResponse(responseCode = "404", description = "User or game not found")
    public ResponseEntity<UserResponse> addGameToLibrary(
            @PathVariable("userId") Long userId,
            @PathVariable("gameId") Long gameId
    ) {
        return ResponseEntity.ok(userService.addGameToLibrary(userId, gameId));
    }

    @PostMapping("/{userId}/wishlist/{gameId}")
    @Operation(summary = "Add game to user's wishlist")
    @ApiResponse(responseCode = "200", description = "Game added to wishlist")
    @ApiResponse(responseCode = "404", description = "User or game not found")
    public ResponseEntity<UserResponse> addGameToWishlist(
            @PathVariable("userId") Long userId,
            @PathVariable("gameId") Long gameId
    ) {
        return ResponseEntity.ok(userService.addGameToWishlist(userId, gameId));
    }

    @PostMapping
    @Operation(summary = "Create user")
    @ApiResponse(responseCode = "201", description = "User created")
    @ApiResponse(responseCode = "400", description = "Validation error")
    public ResponseEntity<UserResponse> create(@Valid @RequestBody UserRequest request) {
        UserResponse response = userService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update user")
    @ApiResponse(responseCode = "200", description = "User updated")
    @ApiResponse(responseCode = "400", description = "Validation error")
    @ApiResponse(responseCode = "404", description = "User not found")
    public ResponseEntity<UserResponse> update(
            @PathVariable("id") Long id,
            @Valid @RequestBody UserRequest request
    ) {
        return ResponseEntity.ok(userService.update(id, request));
    }

    @DeleteMapping("/{userId}/games/{gameId}")
    @Operation(summary = "Remove game from user's library")
    @ApiResponse(responseCode = "200", description = "Game removed from library")
    @ApiResponse(responseCode = "404", description = "User or game not found")
    public ResponseEntity<UserResponse> removeGameFromLibrary(
            @PathVariable("userId") Long userId,
            @PathVariable("gameId") Long gameId
    ) {
        return ResponseEntity.ok(userService.removeGameFromLibrary(userId, gameId));
    }

    @DeleteMapping("/{userId}/wishlist/{gameId}")
    @Operation(summary = "Remove game from user's wishlist")
    @ApiResponse(responseCode = "200", description = "Game removed from wishlist")
    @ApiResponse(responseCode = "404", description = "User or game not found")
    public ResponseEntity<UserResponse> removeGameFromWishlist(
            @PathVariable("userId") Long userId,
            @PathVariable("gameId") Long gameId
    ) {
        return ResponseEntity.ok(userService.removeGameFromWishlist(userId, gameId));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete user")
    @ApiResponse(responseCode = "204", description = "User deleted")
    @ApiResponse(responseCode = "404", description = "User not found")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }
}