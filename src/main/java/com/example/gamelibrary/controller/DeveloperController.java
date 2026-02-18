package com.example.gamelibrary.controller;

import com.example.gamelibrary.dto.request.DeveloperRequest;
import com.example.gamelibrary.dto.response.DeveloperResponse;
import com.example.gamelibrary.dto.response.GameResponse;
import com.example.gamelibrary.service.DeveloperService;
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
@RequestMapping("/api/developers")
@Tag(name = "Developers", description = "Game developers CRUD operations")
public class DeveloperController {

    private final DeveloperService developerService;

    @GetMapping
    @Operation(summary = "Get all developers")
    @ApiResponse(responseCode = "200", description = "Developers returned")
    public ResponseEntity<List<DeveloperResponse>> getAll() {
        return ResponseEntity.ok(developerService.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get developer by id")
    @ApiResponse(responseCode = "200", description = "Developer found")
    @ApiResponse(responseCode = "404", description = "Developer not found")
    public ResponseEntity<DeveloperResponse> getById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(developerService.findById(id));
    }

    @GetMapping("/{id}/games")
    @Operation(summary = "Get all games by developer")
    @ApiResponse(responseCode = "200", description = "Games returned")
    @ApiResponse(responseCode = "404", description = "Developer not found")
    public ResponseEntity<List<GameResponse>> getGamesByDeveloper(@PathVariable("id") Long id) {
        return ResponseEntity.ok(developerService.findGamesByDeveloperId(id));
    }

    @GetMapping("/search")
    @Operation(summary = "Search developers by name")
    @ApiResponse(responseCode = "200", description = "Developers returned")
    public ResponseEntity<List<DeveloperResponse>> searchByName(@RequestParam("name") String name) {
        return ResponseEntity.ok(developerService.searchByName(name));
    }

    @PostMapping
    @Operation(summary = "Create developer")
    @ApiResponse(responseCode = "201", description = "Developer created")
    @ApiResponse(responseCode = "400", description = "Validation error")
    public ResponseEntity<DeveloperResponse> create(@Valid @RequestBody DeveloperRequest request) {
        DeveloperResponse response = developerService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update developer")
    @ApiResponse(responseCode = "200", description = "Developer updated")
    @ApiResponse(responseCode = "400", description = "Validation error")
    @ApiResponse(responseCode = "404", description = "Developer not found")
    public ResponseEntity<DeveloperResponse> update(
            @PathVariable("id") Long id,
            @Valid @RequestBody DeveloperRequest request
    ) {
        return ResponseEntity.ok(developerService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete developer")
    @ApiResponse(responseCode = "204", description = "Developer deleted")
    @ApiResponse(responseCode = "404", description = "Developer not found")
    @ApiResponse(responseCode = "409", description = "Developer has games assigned")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        developerService.delete(id);
        return ResponseEntity.noContent().build();
    }
}