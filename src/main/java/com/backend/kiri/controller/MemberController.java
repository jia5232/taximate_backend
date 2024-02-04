package com.backend.kiri.controller;

import com.backend.kiri.service.dto.member.JoinDto;
import com.backend.kiri.service.MemberService;
import com.backend.kiri.service.dto.member.MemberDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity token(@RequestHeader("Authorization") String authorization){
        String refreshToken = authorization.split(" ")[1];
        Map<String, String> tokens = memberService.createNewTokens(refreshToken);

        HttpHeaders headers = new HttpHeaders();
        headers.set("accessToken", "Bearer "+tokens.get("accessToken"));
        headers.set("refreshToken", "Bearer "+tokens.get("refreshToken"));

        return new ResponseEntity<>(headers, HttpStatus.OK);
    }

    @GetMapping("/member") //멤버 정보 조회
    public MemberDto getMember(@RequestHeader("Authorization") String authorization){
        String refreshToken = authorization.split(" ")[1];
        MemberDto member = memberService.getMember(refreshToken);
        return member;
    }

    @GetMapping("/test")
    public String testLogin(){
        return "testLogin";
    }
}
