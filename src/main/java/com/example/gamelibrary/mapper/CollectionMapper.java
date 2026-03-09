package com.example.gamelibrary.mapper;

import com.example.gamelibrary.model.dto.request.CollectionRequest;
import com.example.gamelibrary.model.dto.response.CollectionResponse;
import com.example.gamelibrary.model.entity.Collection;
import com.example.gamelibrary.model.entity.Game;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CollectionMapper {
    @Mapping(source = "owner.id", target = "ownerId")
    @Mapping(source = "games", target = "gameIds")
    CollectionResponse toResponse(Collection collection);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "owner", ignore = true)
    @Mapping(target = "games", ignore = true)
    Collection fromRequest(CollectionRequest request);

    @Mapping(source = "owner.id", target = "ownerId")
    @Mapping(source = "games", target = "gameIds")
    CollectionRequest toRequest(Collection collection);

    default Set<Long> mapGamesToIds(Set<Game> games) {
        if (games == null || games.isEmpty()) {
            return Collections.emptySet();
        }
        return games.stream()
                .map(Game::getId)
                .collect(Collectors.toSet());
    }
}
