package com.example.gamelibrary.model.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CollectionRequest {
    @Schema(description = "Collection name", example = "Favorites")
    @NotBlank
    @Size(max = 150)
    private String name;

    @Schema(description = "Owner user id", example = "1")
    @NotNull
    private Long ownerId;

    @Schema(description = "Initial game ids")
    private Set<Long> gameIds;
}
