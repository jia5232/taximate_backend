package com.backend.kiri.service;

import com.backend.kiri.domain.university.University;
import com.backend.kiri.repository.university.UniversityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class UniversityService {
    private final UniversityRepository universityRepository;

    public List<String> searchUniversities(String query) {
        return universityRepository.findByNameContainingIgnoreCase(query)
                .stream()
                .map(University::getName)
                .collect(Collectors.toList());
    }
}
