package com.example.gamelibrary.mapper;

import com.example.gamelibrary.model.dto.request.DeveloperRequest;
import com.example.gamelibrary.model.dto.response.DeveloperResponse;
import com.example.gamelibrary.model.entity.Developer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface DeveloperMapper {
    DeveloperResponse toResponse(Developer developer);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "games", ignore = true)
    Developer fromRequest(DeveloperRequest request);

    DeveloperRequest toRequest(Developer developer);
}
