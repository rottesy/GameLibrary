package com.example.gamelibrary.controller;

import com.example.gamelibrary.model.dto.request.DeveloperRequest;
import com.example.gamelibrary.model.dto.response.DeveloperResponse;
import com.example.gamelibrary.model.dto.response.GameResponse;
import com.example.gamelibrary.service.DeveloperService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
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
@RequestMapping("/api/developers")
@Validated
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
    public ResponseEntity<DeveloperResponse> getById(@PathVariable("id") @Positive Long id) {
        return ResponseEntity.ok(developerService.findById(id));
    }

    @GetMapping("/{id}/games")
    @Operation(summary = "Get all games by developer")
    @ApiResponse(responseCode = "200", description = "Games returned")
    @ApiResponse(responseCode = "404", description = "Developer not found")
    public ResponseEntity<List<GameResponse>> getGamesByDeveloper(@PathVariable("id") @Positive Long id) {
        return ResponseEntity.ok(developerService.findGamesByDeveloperId(id));
    }

    @GetMapping("/search")
    @Operation(summary = "Search developers by name")
    @ApiResponse(responseCode = "200", description = "Developers returned")
    public ResponseEntity<List<DeveloperResponse>> searchByName(@RequestParam("name") @NotBlank String name) {
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
            @PathVariable("id") @Positive Long id,
            @Valid @RequestBody DeveloperRequest request
    ) {
        return ResponseEntity.ok(developerService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete developer")
    @ApiResponse(responseCode = "204", description = "Developer deleted")
    @ApiResponse(responseCode = "404", description = "Developer not found")
    @ApiResponse(responseCode = "409", description = "Developer has games assigned")
    public ResponseEntity<Void> delete(@PathVariable("id") @Positive Long id) {
        developerService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
