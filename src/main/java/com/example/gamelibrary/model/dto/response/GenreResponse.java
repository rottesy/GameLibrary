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
public class GenreResponse {
    @Schema(description = "Genre id", example = "1")
    private Long id;

    @Schema(description = "Genre name", example = "RPG")
    private String name;
}
