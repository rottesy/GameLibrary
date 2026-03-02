package com.example.gamelibrary.model.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
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
public class GameResponse {
    @Schema(description = "Game id", example = "1")
    private Long id;

    @Schema(description = "Game title", example = "The Witcher 3")
    private String title;

    @Schema(description = "Game description")
    private String description;

    @Schema(description = "Release date", example = "2015-05-19")
    private LocalDate releaseDate;

    @Schema(description = "Rating (1-10)", example = "9")
    private Integer rating;

    @Schema(description = "Developer id", example = "1")
    private Long developerId;

    @Schema(description = "Genre ids")
    private Set<Long> genreIds;
}
