package com.example.gamelibrary.controller;

import com.example.gamelibrary.model.dto.request.CollectionRequest;
import com.example.gamelibrary.model.dto.response.CollectionResponse;
import com.example.gamelibrary.model.dto.response.GameResponse;
import com.example.gamelibrary.service.CollectionService;
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
@RequestMapping("/api/collections")
@Tag(name = "Game Collections", description = "Game collection CRUD operations")
public class CollectionController {

    private final CollectionService collectionService;

    @GetMapping
    @Operation(summary = "Get all game collections")
    @ApiResponse(responseCode = "200", description = "Collections returned")
    public ResponseEntity<List<CollectionResponse>> getAll() {
        return ResponseEntity.ok(collectionService.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get collection by id")
    @ApiResponse(responseCode = "200", description = "Collection found")
    @ApiResponse(responseCode = "404", description = "Collection not found")
    public ResponseEntity<CollectionResponse> getById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(collectionService.findById(id));
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get all collections by user")
    @ApiResponse(responseCode = "200", description = "Collections returned")
    public ResponseEntity<List<CollectionResponse>> getByUserId(@PathVariable("userId") Long userId) {
        return ResponseEntity.ok(collectionService.findByUserId(userId));
    }

    @GetMapping("/{id}/games")
    @Operation(summary = "Get all games in a collection")
    @ApiResponse(responseCode = "200", description = "Games returned")
    @ApiResponse(responseCode = "404", description = "Collection not found")
    public ResponseEntity<List<GameResponse>> getGamesInCollection(@PathVariable("id") Long id) {
        return ResponseEntity.ok(collectionService.findGamesInCollection(id));
    }

    @PostMapping
    @Operation(summary = "Create collection")
    @ApiResponse(responseCode = "201", description = "Collection created")
    @ApiResponse(responseCode = "400", description = "Validation error")
    public ResponseEntity<CollectionResponse> create(@Valid @RequestBody CollectionRequest request) {
        CollectionResponse response = collectionService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/{collectionId}/games/{gameId}")
    @Operation(summary = "Add game to collection")
    @ApiResponse(responseCode = "200", description = "Game added to collection")
    @ApiResponse(responseCode = "404", description = "Collection or game not found")
    public ResponseEntity<CollectionResponse> addGameToCollection(
            @PathVariable("collectionId") Long collectionId,
            @PathVariable("gameId") Long gameId
    ) {
        return ResponseEntity.ok(collectionService.addGameToCollection(collectionId, gameId));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update collection")
    @ApiResponse(responseCode = "200", description = "Collection updated")
    @ApiResponse(responseCode = "400", description = "Validation error")
    @ApiResponse(responseCode = "404", description = "Collection not found")
    public ResponseEntity<CollectionResponse> update(
            @PathVariable("id") Long id,
            @Valid @RequestBody CollectionRequest request
    ) {
        return ResponseEntity.ok(collectionService.update(id, request));
    }

    @DeleteMapping("/{collectionId}/games/{gameId}")
    @Operation(summary = "Remove game from collection")
    @ApiResponse(responseCode = "200", description = "Game removed from collection")
    @ApiResponse(responseCode = "404", description = "Collection or game not found")
    public ResponseEntity<CollectionResponse> removeGameFromCollection(
            @PathVariable("collectionId") Long collectionId,
            @PathVariable("gameId") Long gameId
    ) {
        return ResponseEntity.ok(collectionService.removeGameFromCollection(collectionId, gameId));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete collection")
    @ApiResponse(responseCode = "204", description = "Collection deleted")
    @ApiResponse(responseCode = "404", description = "Collection not found")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        collectionService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
