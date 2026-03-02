package com.example.gamelibrary.model.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewResponse {
    @Schema(description = "Review id", example = "1")
    private Long id;

    @Schema(description = "Rating (1-10)", example = "8")
    private Integer rating;

    @Schema(description = "Review comment")
    private String comment;

    @Schema(description = "Created at", example = "2024-01-15T12:34:56")
    private LocalDateTime createdAt;

    @Schema(description = "Game id", example = "1")
    private Long gameId;

    @Schema(description = "User id", example = "1")
    private Long userId;
}
