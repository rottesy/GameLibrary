package com.example.gamelibrary.mapper;

import com.example.gamelibrary.model.dto.request.ReviewRequest;
import com.example.gamelibrary.model.dto.response.ReviewResponse;
import com.example.gamelibrary.model.entity.Review;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ReviewMapper {
    @Mapping(source = "game.id", target = "gameId")
    @Mapping(source = "user.id", target = "userId")
    ReviewResponse toResponse(Review review);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "game", ignore = true)
    @Mapping(target = "user", ignore = true)
    Review fromRequest(ReviewRequest request);

    @Mapping(source = "game.id", target = "gameId")
    @Mapping(source = "user.id", target = "userId")
    ReviewRequest toRequest(Review review);
}
