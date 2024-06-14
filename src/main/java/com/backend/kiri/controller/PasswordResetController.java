package com.backend.kiri.controller;

import com.backend.kiri.service.PasswordResetService;
import com.backend.kiri.service.dto.password.EmailDto;
import com.backend.kiri.service.dto.password.PasswordResetDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/password")
public class PasswordResetController {
    private final PasswordResetService passwordResetService;

    @PostMapping("/reset/request")
    public ResponseEntity<Map<String, String>> requestPasswordReset(@RequestBody EmailDto emailDto) {
        Map<String, String> response = passwordResetService.requestPasswordReset(emailDto.getEmail());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/reset/confirm")
    public ResponseEntity<Void> resetPassword(@RequestBody PasswordResetDto passwordResetDto) {
        passwordResetService.resetPassword(passwordResetDto.getEmail(), passwordResetDto.getNewPassword());
        return ResponseEntity.ok().build();
    }
}


