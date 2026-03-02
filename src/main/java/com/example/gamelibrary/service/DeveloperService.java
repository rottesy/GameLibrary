package com.example.gamelibrary.service;

import com.example.gamelibrary.model.dto.request.DeveloperRequest;
import com.example.gamelibrary.model.dto.response.DeveloperResponse;
import com.example.gamelibrary.model.dto.response.GameResponse;
import java.util.List;

public interface DeveloperService {
    List<DeveloperResponse> findAll();

    DeveloperResponse findById(Long id);

    List<GameResponse> findGamesByDeveloperId(Long id);

    List<DeveloperResponse> searchByName(String name);

    DeveloperResponse create(DeveloperRequest request);

    DeveloperResponse update(Long id, DeveloperRequest request);

    void delete(Long id);
}
