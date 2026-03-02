package com.example.gamelibrary.model.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GameCompositeRequest {
    @Schema(description = "Game data")
    @Valid
    @NotNull
    private GameRequest game;

    @Schema(description = "Review data")
    @Valid
    @NotNull
    private ReviewRequest review;

    @Schema(description = "Achievement data")
    @Valid
    @NotNull
    private AchievementRequest achievement;
}
