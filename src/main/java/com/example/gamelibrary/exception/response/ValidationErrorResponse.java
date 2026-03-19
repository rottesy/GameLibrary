package com.example.gamelibrary.exception.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.Map;

@Schema(description = "Validation error response")
public record ValidationErrorResponse(
        @Schema(description = "HTTP status code", example = "400")
        int status,
        @Schema(description = "Validation error message", example = "Validation failed")
        String message,
        @Schema(description = "Timestamp when the error occurred", example = "2026-03-19T21:00:00")
        LocalDateTime timestamp,
        @Schema(description = "Validation errors by field or parameter name")
        Map<String, String> errors
) {
}
