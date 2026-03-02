package com.example.gamelibrary.model.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AchievementResponse {
    @Schema(description = "Achievement id", example = "1")
    private Long id;

    @Schema(description = "Achievement name", example = "Master Explorer")
    private String name;

    @Schema(description = "Achievement description")
    private String description;

    @Schema(description = "Game id", example = "1")
    private Long gameId;
}
