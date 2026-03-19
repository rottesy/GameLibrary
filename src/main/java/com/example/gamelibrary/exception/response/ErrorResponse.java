package com.example.gamelibrary.exception.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(description = "Standard error response")
public record ErrorResponse(
        @Schema(description = "HTTP status code", example = "404")
        int status,
        @Schema(description = "Error message", example = "Game not found: 99")
        String message,
        @Schema(description = "Timestamp when the error occurred", example = "2026-03-19T21:00:00")
        LocalDateTime timestamp
) {
}
