package com.example.gamelibrary.model.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CollectionResponse {
    @Schema(description = "Collection id", example = "1")
    private Long id;

    @Schema(description = "Collection name", example = "Favorites")
    private String name;

    @Schema(description = "Owner user id", example = "1")
    private Long ownerId;

    @Schema(description = "Game ids in collection")
    private Set<Long> gameIds;
}
