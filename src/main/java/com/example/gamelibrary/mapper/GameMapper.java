package com.example.gamelibrary.mapper;

import com.example.gamelibrary.model.dto.request.GameRequest;
import com.example.gamelibrary.model.dto.response.GameResponse;
import com.example.gamelibrary.model.entity.Game;
import com.example.gamelibrary.model.entity.Genre;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface GameMapper {
    @Mapping(source = "developer.id", target = "developerId")
    @Mapping(source = "genres", target = "genreIds")
    GameResponse toResponse(Game game);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "developer", ignore = true)
    @Mapping(target = "genres", ignore = true)
    @Mapping(target = "reviews", ignore = true)
    @Mapping(target = "achievements", ignore = true)
    @Mapping(target = "owners", ignore = true)
    @Mapping(target = "wishlistedBy", ignore = true)
    @Mapping(target = "collections", ignore = true)
    Game fromRequest(GameRequest request);

    GameRequest toRequest(Game game);

    default Set<Long> mapGenresToIds(Set<Genre> genres) {
        if (genres == null || genres.isEmpty()) {
            return Collections.emptySet();
        }
        return genres.stream()
                .map(Genre::getId)
                .collect(Collectors.toSet());
    }
}
