package com.example.gamelibrary.model.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GameWithReviewsResponse {
    @Schema(description = "Game data")
    private GameResponse game;

    @Schema(description = "Game reviews")
    private List<ReviewResponse> reviews;
}
