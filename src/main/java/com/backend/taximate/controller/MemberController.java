package com.backend.taximate.controller;

import com.backend.taximate.service.dto.member.JoinDto;
import com.backend.taximate.service.MemberService;
import com.backend.taximate.service.dto.member.MemberDto;
import com.backend.taximate.service.dto.member.signup.EmailDto;
import com.backend.taximate.service.dto.member.signup.EmailSuffixDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;

    // 인증 이메일을 보내주는 api, 중복 이메일 확인도 이 api 하나로 가능하도록 service단에서 구현했다.
    @PostMapping("/email")
    public Map<String, String> sendEmail(@RequestBody EmailDto emailDto) throws Exception{
        return memberService.sendEmail(emailDto.getEmail());
    }

    @GetMapping("/nicknameExists")
    public ResponseEntity<Boolean> checkNicknameDuplicate(@RequestParam String nickname){
        return ResponseEntity.ok(memberService.checkNicknameDuplicate(nickname));
    }

    @PostMapping("/validateEmailSuffix")
    public ResponseEntity<Boolean> validateEmailSuffix(@RequestBody EmailSuffixDto emailSuffixDto){
        return ResponseEntity.ok(memberService.validateEmailSuffix(emailSuffixDto));
    }

    @PostMapping("/signup")
    public String signUp(@RequestBody JoinDto joinDto){
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

    @DeleteMapping("/member")
    public ResponseEntity<Void> deleteMember(@RequestHeader("Authorization") String authorization) {
        String accessToken = authorization.split(" ")[1];
        memberService.deleteMember(accessToken);
        return ResponseEntity.noContent().build();
    }
}
