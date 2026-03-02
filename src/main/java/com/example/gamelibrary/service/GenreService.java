package com.example.gamelibrary.service;

import com.example.gamelibrary.model.dto.request.GenreRequest;
import com.example.gamelibrary.model.dto.response.GameResponse;
import com.example.gamelibrary.model.dto.response.GenreResponse;
import java.util.List;

public interface GenreService {
    List<GenreResponse> findAll();

    GenreResponse findById(Long id);

    List<GameResponse> findGamesByGenreId(Long id);

    List<GenreResponse> searchByName(String name);

    GenreResponse create(GenreRequest request);

    GenreResponse update(Long id, GenreRequest request);

    void delete(Long id);
}
