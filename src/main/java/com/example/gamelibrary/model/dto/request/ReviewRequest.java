package com.example.gamelibrary.model.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewRequest {
    @Schema(description = "Rating (1-10)", example = "8")
    @NotNull
    @Min(1)
    @Max(10)
    private Integer rating;

    @Schema(description = "Review comment")
    @Size(max = 2000)
    private String comment;

    @Schema(description = "Game id", example = "1")
    @NotNull
    private Long gameId;

    @Schema(description = "User id", example = "1")
    @NotNull
    private Long userId;
}
