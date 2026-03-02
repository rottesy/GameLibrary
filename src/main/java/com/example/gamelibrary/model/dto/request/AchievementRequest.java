package com.example.gamelibrary.model.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
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
public class AchievementRequest {
    @Schema(description = "Achievement name", example = "Master Explorer")
    @NotBlank
    @Size(max = 200)
    private String name;

    @Schema(description = "Achievement description")
    @Size(max = 2000)
    private String description;

    @Schema(description = "Game id", example = "1")
    @NotNull
    private Long gameId;
}
