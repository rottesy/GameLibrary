package com.example.gamelibrary.repository.projection;

import java.time.LocalDate;

public interface GameAchievementSearchNativeProjection {
    Long getId();

    String getTitle();

    String getDescription();

    LocalDate getReleaseDate();

    Integer getRating();

    Long getDeveloperId();

    String getGenreIdsCsv();
}
