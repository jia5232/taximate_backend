package com.backend.taximate.controller;

import com.backend.taximate.service.PasswordChangeService;
import com.backend.taximate.service.dto.password.PasswordChangeDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/password")
public class PasswordChangeController {
    private final PasswordChangeService passwordChangeService;

    @PostMapping("/change")
    public ResponseEntity<Void> changePassword(
            @RequestHeader("Authorization") String accessToken,
            @RequestBody PasswordChangeDto passwordChangeDto) {
        String token = accessToken.replace("Bearer ", "");
        passwordChangeService.changePassword(
                token,
                passwordChangeDto.getCurrentPassword(),
                passwordChangeDto.getNewPassword()
        );
        return ResponseEntity.ok().build();
    }
}

