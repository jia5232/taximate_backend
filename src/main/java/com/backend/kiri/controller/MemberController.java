package com.backend.kiri.controller;

import com.backend.kiri.service.dto.JoinDto;
import com.backend.kiri.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;

    @PostMapping("/signup")
    public String signUp(@RequestBody JoinDto joinDto){
        System.out.println(joinDto);
        memberService.joinProcess(joinDto);
        return "ok";
    }

    @PostMapping("/token") //토큰 재발급
    public Map<String, String> token(@RequestHeader("Authorization") String authorization){
        String refreshToken = authorization.split(" ")[1];
        return memberService.createNewTokens(refreshToken);
    }

    @GetMapping("/test")
    public String testLogin(){
        return "testLogin";
    }
}
