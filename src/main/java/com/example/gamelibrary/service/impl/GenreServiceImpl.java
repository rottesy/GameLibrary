package com.example.gamelibrary.service.impl;

import com.example.gamelibrary.exception.GenreNotFoundException;
import com.example.gamelibrary.mapper.GameMapper;
import com.example.gamelibrary.mapper.GenreMapper;
import com.example.gamelibrary.model.dto.request.GenreRequest;
import com.example.gamelibrary.model.dto.response.GameResponse;
import com.example.gamelibrary.model.dto.response.GenreResponse;
import com.example.gamelibrary.model.entity.Genre;
import com.example.gamelibrary.repository.GameRepository;
import com.example.gamelibrary.repository.GenreRepository;
import com.example.gamelibrary.service.GenreService;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@AllArgsConstructor
public class GenreServiceImpl implements GenreService {
    private final GenreRepository genreRepository;
    private final GameRepository gameRepository;
    private final GenreMapper genreMapper;
    private final GameMapper gameMapper;

    @Override
    public List<GenreResponse> findAll() {
        return genreRepository.findAll().stream()
                .map(genreMapper::toResponse)
                .toList();
    }

    @Override
    public GenreResponse findById(Long id) {
        Genre genre = genreRepository.findById(id)
                .orElseThrow(() -> new GenreNotFoundException("Genre not found: " + id));
        return genreMapper.toResponse(genre);
    }

    @Override
    public List<GameResponse> findGamesByGenreId(Long id) {
        if (!genreRepository.existsById(id)) {
            throw new GenreNotFoundException("Genre not found: " + id);
        }
        return gameRepository.findByGenres_Id(id).stream()
                .map(gameMapper::toResponse)
                .toList();
    }

    @Override
    public List<GenreResponse> searchByName(String name) {
        return genreRepository.findByNameContainingIgnoreCase(name).stream()
                .map(genreMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public GenreResponse create(GenreRequest request) {
        Genre genre = genreMapper.fromRequest(request);
        Genre saved = genreRepository.save(genre);
        return genreMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public GenreResponse update(Long id, GenreRequest request) {
        Genre genre = genreRepository.findById(id)
                .orElseThrow(() -> new GenreNotFoundException("Genre not found: " + id));
        genre.setName(request.getName());
        Genre saved = genreRepository.save(genre);
        return genreMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!genreRepository.existsById(id)) {
            throw new GenreNotFoundException("Genre not found: " + id);
        }
        genreRepository.deleteById(id);
    }
}
