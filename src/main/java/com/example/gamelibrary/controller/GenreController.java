package com.example.gamelibrary.controller;

import com.example.gamelibrary.model.dto.request.GenreRequest;
import com.example.gamelibrary.model.dto.response.GenreResponse;
import com.example.gamelibrary.model.dto.response.GameResponse;
import com.example.gamelibrary.service.GenreService;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/api/genres")
@Tag(name = "Genres", description = "Game genres CRUD operations")
public class GenreController {

    private final GenreService genreService;

    @GetMapping
    @Operation(summary = "Get all genres")
    @ApiResponse(responseCode = "200", description = "Genres returned")
    public ResponseEntity<List<GenreResponse>> getAll() {
        return ResponseEntity.ok(genreService.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get genre by id")
    @ApiResponse(responseCode = "200", description = "Genre found")
    @ApiResponse(responseCode = "404", description = "Genre not found")
    public ResponseEntity<GenreResponse> getById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(genreService.findById(id));
    }

    @GetMapping("/{id}/games")
    @Operation(summary = "Get all games by genre")
    @ApiResponse(responseCode = "200", description = "Games returned")
    @ApiResponse(responseCode = "404", description = "Genre not found")
    public ResponseEntity<List<GameResponse>> getGamesByGenre(@PathVariable("id") Long id) {
        return ResponseEntity.ok(genreService.findGamesByGenreId(id));
    }

    @GetMapping("/search")
    @Operation(summary = "Search genres by name")
    @ApiResponse(responseCode = "200", description = "Genres returned")
    public ResponseEntity<List<GenreResponse>> searchByName(@RequestParam("name") String name) {
        return ResponseEntity.ok(genreService.searchByName(name));
    }

    @PostMapping
    @Operation(summary = "Create genre")
    @ApiResponse(responseCode = "201", description = "Genre created")
    @ApiResponse(responseCode = "400", description = "Validation error")
    public ResponseEntity<GenreResponse> create(@Valid @RequestBody GenreRequest request) {
        GenreResponse response = genreService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update genre")
    @ApiResponse(responseCode = "200", description = "Genre updated")
    @ApiResponse(responseCode = "400", description = "Validation error")
    @ApiResponse(responseCode = "404", description = "Genre not found")
    public ResponseEntity<GenreResponse> update(
            @PathVariable("id") Long id,
            @Valid @RequestBody GenreRequest request
    ) {
        return ResponseEntity.ok(genreService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete genre")
    @ApiResponse(responseCode = "204", description = "Genre deleted")
    @ApiResponse(responseCode = "404", description = "Genre not found")
    @ApiResponse(responseCode = "409", description = "Genre has games assigned")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        genreService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
