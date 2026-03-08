package com.example.gamelibrary.service.impl;

import com.example.gamelibrary.exception.DeveloperNotFoundException;
import com.example.gamelibrary.mapper.DeveloperMapper;
import com.example.gamelibrary.mapper.GameMapper;
import com.example.gamelibrary.model.dto.request.DeveloperRequest;
import com.example.gamelibrary.model.dto.response.DeveloperResponse;
import com.example.gamelibrary.model.dto.response.GameResponse;
import com.example.gamelibrary.model.entity.Developer;
import com.example.gamelibrary.repository.DeveloperRepository;
import com.example.gamelibrary.repository.GameRepository;
import com.example.gamelibrary.service.DeveloperService;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@AllArgsConstructor
public class DeveloperServiceImpl implements DeveloperService {
    private final DeveloperRepository developerRepository;
    private final GameRepository gameRepository;
    private final DeveloperMapper developerMapper;
    private final GameMapper gameMapper;

    @Override
    public List<DeveloperResponse> findAll() {
        return developerRepository.findAll().stream()
                .map(developerMapper::toResponse)
                .toList();
    }

    @Override
    public DeveloperResponse findById(Long id) {
        Developer developer = developerRepository.findById(id)
                .orElseThrow(() -> new DeveloperNotFoundException("Developer not found: " + id));
        return developerMapper.toResponse(developer);
    }

    @Override
    public List<GameResponse> findGamesByDeveloperId(Long id) {
        if (!developerRepository.existsById(id)) {
            throw new DeveloperNotFoundException("Developer not found: " + id);
        }
        return gameRepository.findByDeveloperId(id).stream()
                .map(gameMapper::toResponse)
                .toList();
    }

    @Override
    public List<DeveloperResponse> searchByName(String name) {
        return developerRepository.findByNameContainingIgnoreCase(name).stream()
                .map(developerMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public DeveloperResponse create(DeveloperRequest request) {
        Developer developer = developerMapper.fromRequest(request);
        Developer saved = developerRepository.save(developer);
        return developerMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public DeveloperResponse update(Long id, DeveloperRequest request) {
        Developer developer = developerRepository.findById(id)
                .orElseThrow(() -> new DeveloperNotFoundException("Developer not found: " + id));
        developer.setName(request.getName());
        Developer saved = developerRepository.save(developer);
        return developerMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!developerRepository.existsById(id)) {
            throw new DeveloperNotFoundException("Developer not found: " + id);
        }
        developerRepository.deleteById(id);
    }
}
