package com.backend.kiri.controller;

import com.backend.kiri.service.dto.member.JoinDto;
import com.backend.kiri.service.MemberService;
import com.backend.kiri.service.dto.member.MemberDto;
import com.backend.kiri.service.dto.member.signup.EmailDto;
import com.backend.kiri.service.dto.member.signup.NicknameDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;
    @PostMapping("/email")
    public Map<String, String> sendEmail(@RequestBody EmailDto emailDto) throws Exception{
        return memberService.sendEmail(emailDto.getEmail());
    }

    @GetMapping("/nicknameExists")
    public ResponseEntity<Boolean> checkNicknameDuplicate(@RequestBody NicknameDto nicknameDto){
        return ResponseEntity.ok(memberService.checkNicknameDuplicate(nicknameDto.getNickname()));
    }


    @PostMapping("/signup")
    public String signUp(@RequestBody JoinDto joinDto){
        System.out.println(joinDto);
        memberService.joinProcess(joinDto);
        return "ok";
    }

    @PostMapping("/token") //토큰 재발급
    public Map<String, String> token(@RequestHeader("Authorization") String authorization){
        String refreshToken = authorization.split(" ")[1];
        Map<String, String> tokens = memberService.createNewTokens(refreshToken);
        // 바디에 담아 반환
        return tokens;
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
