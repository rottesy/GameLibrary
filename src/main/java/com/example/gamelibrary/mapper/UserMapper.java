package com.example.gamelibrary.mapper;

import com.example.gamelibrary.model.dto.request.UserRequest;
import com.example.gamelibrary.model.dto.response.UserResponse;
import com.example.gamelibrary.model.dto.response.UserSummaryResponse;
import com.example.gamelibrary.model.entity.Game;
import com.example.gamelibrary.model.entity.User;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(source = "libraryGames", target = "libraryGameIds")
    @Mapping(source = "wishlistGames", target = "wishlistGameIds")
    UserResponse toResponse(User user);

    UserSummaryResponse toSummaryResponse(User user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "libraryGames", ignore = true)
    @Mapping(target = "wishlistGames", ignore = true)
    @Mapping(target = "reviews", ignore = true)
    @Mapping(target = "collections", ignore = true)
    User fromRequest(UserRequest request);

    UserRequest toRequest(User user);

    default Set<Long> mapGamesToIds(Set<Game> games) {
        if (games == null || games.isEmpty()) {
            return Collections.emptySet();
        }
        return games.stream()
                .map(Game::getId)
                .collect(Collectors.toSet());
    }
}
