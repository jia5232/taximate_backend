package com.backend.kiri.controller;

import com.backend.kiri.domain.university.University;
import com.backend.kiri.service.UniversityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class UniversityController {
    private final UniversityService universityService;

    @GetMapping("/universities/search")
    public ResponseEntity<List<String>> searchUniversities(@RequestParam String searchKeyword) {
        List<String> universityNames = universityService.searchUniversities(searchKeyword);
        return ResponseEntity.ok(universityNames);
    }
}
