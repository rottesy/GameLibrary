package com.example.gamelibrary.model.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GameRequest {
    @Schema(description = "Game title", example = "The Witcher 3")
    @NotBlank
    @Size(max = 200)
    private String title;

    @Schema(description = "Game description")
    @Size(max = 2000)
    private String description;

    @Schema(description = "Release date", example = "2015-05-19")
    private LocalDate releaseDate;

    @Schema(description = "Rating (1-10)", example = "9")
    @Min(1)
    @Max(10)
    private Integer rating;

    @Schema(description = "Developer id", example = "1")
    @NotNull
    private Long developerId;

    @Schema(description = "Genre ids")
    private Set<Long> genreIds;
}
