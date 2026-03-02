package com.example.gamelibrary.controller;

import com.example.gamelibrary.model.dto.request.GameRequest;
import com.example.gamelibrary.model.dto.request.GameCompositeRequest;
import com.example.gamelibrary.model.dto.response.GameResponse;
import com.example.gamelibrary.service.GameService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
@RequestMapping("/api/games")
@Tag(name = "Games", description = "Game CRUD operations")
public class GameController {

    private final GameService gameService;

    @GetMapping
    @Operation(summary = "Get all games", description = "Optional filtering by genre, rating or developer")
    @ApiResponse(responseCode = "200", description = "Games returned")
    public ResponseEntity<List<GameResponse>> getAll(
            @Parameter(description = "Game genre filter")
            @RequestParam(name = "genre", required = false) String genre,
            @Parameter(description = "Minimum rating filter (1-10)")
            @RequestParam(name = "minRating", required = false) Integer minRating,
            @Parameter(description = "Developer filter")
            @RequestParam(name = "developer", required = false) String developer
    ) {
        List<GameResponse> response;

        if (genre != null && !genre.isEmpty()) {
            response = gameService.findByGenre(genre);
        } else if (minRating != null) {
            response = gameService.findByMinRating(minRating);
        } else if (developer != null && !developer.isEmpty()) {
            response = gameService.findByDeveloper(developer);
        } else {
            response = gameService.findAll();
        }

        return ResponseEntity.ok(response);
    }

    @GetMapping("/with-reviews")
    @Operation(summary = "Get all games with their reviews")
    @ApiResponse(responseCode = "200", description = "Games with reviews returned")
    public ResponseEntity<List<GameResponse>> getAllWithReviews() {
        return ResponseEntity.ok(gameService.findAllWithReviews());
    }

    @GetMapping("/with-achievements")
    @Operation(summary = "Get all games with achievements")
    @ApiResponse(responseCode = "200", description = "Games with achievements returned")
    public ResponseEntity<List<GameResponse>> getAllWithAchievements() {
        return ResponseEntity.ok(gameService.findAllWithAchievements());
    }

    @GetMapping("/top-rated")
    @Operation(summary = "Get top rated games")
    @ApiResponse(responseCode = "200", description = "Top rated games returned")
    public ResponseEntity<List<GameResponse>> getTopRated(
            @RequestParam(name = "limit", defaultValue = "10") Integer limit
    ) {
        return ResponseEntity.ok(gameService.findTopRated(limit));
    }

    @GetMapping("/new-releases")
    @Operation(summary = "Get new releases")
    @ApiResponse(responseCode = "200", description = "New releases returned")
    public ResponseEntity<List<GameResponse>> getNewReleases(
            @RequestParam(name = "days", defaultValue = "30") Integer days
    ) {
        return ResponseEntity.ok(gameService.findNewReleases(days));
    }

    @GetMapping("/search")
    @Operation(summary = "Search games by keyword")
    @ApiResponse(responseCode = "200", description = "Search results returned")
    public ResponseEntity<List<GameResponse>> search(
            @RequestParam(name = "keyword") String keyword
    ) {
        return ResponseEntity.ok(gameService.searchByKeyword(keyword));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get game by id")
    @ApiResponse(responseCode = "200", description = "Game found")
    @ApiResponse(responseCode = "404", description = "Game not found")
    public ResponseEntity<GameResponse> getById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(gameService.findById(id));
    }

    @PostMapping
    @Operation(summary = "Create game")
    @ApiResponse(responseCode = "201", description = "Game created")
    @ApiResponse(responseCode = "400", description = "Validation error")
    public ResponseEntity<GameResponse> create(@Valid @RequestBody GameRequest request) {
        GameResponse response = gameService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/with-review-and-achievement/no-tx")
    @Operation(summary = "Create game, review and achievement without transaction")
    @ApiResponse(responseCode = "201", description = "Composite data created")
    @ApiResponse(responseCode = "400", description = "Validation error")
    public ResponseEntity<GameResponse> createGameWithReviewAndAchievementNoTx(
            @Valid @RequestBody GameCompositeRequest request
    ) {
        GameResponse response = gameService.createGameWithReviewAndAchievementNoTx(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/with-review-and-achievement/tx")
    @Operation(summary = "Create game, review and achievement with transaction")
    @ApiResponse(responseCode = "201", description = "Composite data created")
    @ApiResponse(responseCode = "400", description = "Validation error")
    public ResponseEntity<GameResponse> createGameWithReviewAndAchievementTx(
            @Valid @RequestBody GameCompositeRequest request
    ) {
        GameResponse response = gameService.createGameWithReviewAndAchievementTx(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update game")
    @ApiResponse(responseCode = "200", description = "Game updated")
    @ApiResponse(responseCode = "400", description = "Validation error")
    @ApiResponse(responseCode = "404", description = "Game not found")
    public ResponseEntity<GameResponse> update(
            @PathVariable("id") Long id,
            @Valid @RequestBody GameRequest request
    ) {
        return ResponseEntity.ok(gameService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete game")
    @ApiResponse(responseCode = "204", description = "Game deleted")
    @ApiResponse(responseCode = "404", description = "Game not found")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        gameService.delete(id);
        return ResponseEntity.noContent().build();
    }
}