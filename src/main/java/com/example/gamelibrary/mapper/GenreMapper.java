package com.example.gamelibrary.mapper;

import com.example.gamelibrary.model.dto.request.GenreRequest;
import com.example.gamelibrary.model.dto.response.GenreResponse;
import com.example.gamelibrary.model.entity.Genre;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface GenreMapper {
    GenreResponse toResponse(Genre genre);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "games", ignore = true)
    Genre fromRequest(GenreRequest request);

    GenreRequest toRequest(Genre genre);
}
