package com.example.gamelibrary.mapper;

import com.example.gamelibrary.model.dto.request.AchievementRequest;
import com.example.gamelibrary.model.dto.response.AchievementResponse;
import com.example.gamelibrary.model.entity.Achievement;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AchievementMapper {
    @Mapping(source = "game.id", target = "gameId")
    AchievementResponse toResponse(Achievement achievement);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "game", ignore = true)
    Achievement fromRequest(AchievementRequest request);

    AchievementRequest toRequest(Achievement achievement);
}
