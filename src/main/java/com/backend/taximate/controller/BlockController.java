package com.backend.taximate.controller;

import com.backend.taximate.service.BlockService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/block")
public class BlockController {
    private final BlockService blockService;

    @PostMapping("/{blockedMemberId}")
    public ResponseEntity<Void> blockMember(@PathVariable Long blockedMemberId, @RequestHeader("Authorization") String authorization) {
        String accessToken = authorization.split(" ")[1];
        blockService.blockMember(blockedMemberId, accessToken);
        return ResponseEntity.ok().build();
    }
}
