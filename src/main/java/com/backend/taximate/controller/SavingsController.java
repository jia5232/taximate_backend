package com.backend.taximate.controller;

import com.backend.taximate.service.SavingsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/savings")
public class SavingsController {
    private final SavingsService savingsService;

    @GetMapping
    public ResponseEntity<Integer> getSavings(@RequestHeader("Authorization") String authorization) {
        String accessToken = authorization.split(" ")[1];
        int savings = savingsService.calculateSavings(accessToken);
        return ResponseEntity.ok(savings);
    }
}