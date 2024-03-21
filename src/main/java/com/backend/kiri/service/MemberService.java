package com.backend.kiri.service;

import com.backend.kiri.domain.Member;
import com.backend.kiri.domain.security.RefreshToken;
import com.backend.kiri.domain.university.University;
import com.backend.kiri.exception.exceptions.*;
import com.backend.kiri.jwt.JWTUtil;
import com.backend.kiri.repository.security.RefreshTokenRepository;
import com.backend.kiri.repository.university.UniversityRepository;
import com.backend.kiri.service.dto.member.JoinDto;
import com.backend.kiri.repository.MemberRepository;
import com.backend.kiri.service.dto.member.MemberDto;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Service
@Transactional
public class MemberService {
    private final MemberRepository memberRepository;
    private final UniversityRepository universityRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JWTUtil jwtUtil;
    private final JavaMailSender javaMailSender;

    private static final long ACCESS_TOKEN_TIME = 1000 * 60 * 30; // 30 분 1000ms(=1s) *60=(1min)*30 =(30min) -> 1000 * 60 * 30L
    private static final long REFRESH_TOKEN_TIME = 1000 * 60 * 60;

    public Map<String, String> sendEmail(String email) {
        HashMap<String, String> map = new HashMap<>();

        SimpleMailMessage message = new SimpleMailMessage();
        int authNumber;

        if(!universityRepository.existsByEmailSuffix(getEmailSuffix(email))){
            throw new NotFoundUniversityException("적절한 대학교 이메일이 아닙니다.");
        }

        if(memberRepository.existsByEmail(email)){
            throw new AlreadyExistMemberException("이미 가입된 회원입니다.");
        }

        SecureRandom secureRandom = new SecureRandom();
        authNumber = 100000 + secureRandom.nextInt(900000);

        message.setTo(email);

        message.setSubject("끼리 회원가입을 위한 이메일 인증번호 메일입니다.");
        message.setText("인증번호는 "+authNumber+"입니다. \n끼리와 함께 오늘도 안전한 등하교 되세요 :)");

        try{
            javaMailSender.send(message);
        } catch(Exception e){
            e.printStackTrace();
        }

        map.put("authNumber", String.valueOf(authNumber));
        return map;
    }

    public String getEmailSuffix(String email) {
        return email.substring(email.lastIndexOf("@") + 1);
    }

    public boolean checkNicknameDuplicate(String nickname){
        return memberRepository.existsByNickname(nickname);
    }

    public void joinProcess(JoinDto joinDto) {
        String email = joinDto.getEmail();
        String password = joinDto.getPassword();
        String nickname = joinDto.getNickname();
        String univName = joinDto.getUnivName();
        Boolean isAccept = joinDto.getIsAccept();
        Boolean isEmailAuthenticated = joinDto.getIsEmailAuthenticated();

        Boolean isEmailExist = memberRepository.existsByEmail(email);
        Boolean isNicknameExist = memberRepository.existsByNickname(nickname);

        University findUniversityByName = universityRepository.findByName(univName)
                .orElseThrow(() -> new NotFoundUniversityException("대학교를 찾을 수 없습니다."));

        Boolean isEmailSuffixValid = findUniversityByName.getEmailSuffix().equals(getEmailSuffix(email));

        if(!isEmailSuffixValid){
            throw new NotEnoughInfoException("대학교와 이메일이 일치하지 않습니다. 올바른 학교 이메일을 입력하세요");
        }

        if (isEmailExist) {
            throw new AlreadyExistMemberException("이미 가입된 회원입니다.");
        }

        if (isNicknameExist) {
            throw new AlreadyExistMemberException("중복된 닉네임입니다.");
        }

        if (isAccept && isEmailAuthenticated) {
            Member member = new Member();
            member.setEmail(email);
            member.setPassword(bCryptPasswordEncoder.encode(password));
            member.setNickname(nickname);
            member.setUnivName(findUniversityByName.getName());
            memberRepository.save(member);
        } else {
            throw new NotEnoughInfoException("회원가입에 필요한 정보가 모두 입력되지 않았습니다.");
        }

    }

    public Map<String, String> createNewTokens(String refreshToken) {
        RefreshToken findToken = refreshTokenRepository.findById(refreshToken)
                .orElseThrow(() -> new NotFoundRefreshTokenException("리프레시 토큰을 찾을 수 없습니다."));

        String email = findToken.getEmail();

        String newAccessToken = jwtUtil.createJwt(email, ACCESS_TOKEN_TIME);
        String newRefreshToken = jwtUtil.createJwt(email, REFRESH_TOKEN_TIME);

        HashMap<String, String> result = new HashMap<>();
        result.put("accessToken", "Bearer " + newAccessToken);
        result.put("refreshToken", "Bearer " + newRefreshToken);

        refreshTokenRepository.delete(findToken);
        RefreshToken createdRefreshToken = new RefreshToken(newRefreshToken, email);
        refreshTokenRepository.save(createdRefreshToken);

        // 이미 세션 생성은 JWTFilter에서 끝났으므로 다시 세션을 생성해줄 필요가 없다.

        return result;
    }

    public MemberDto getMember(String accessToken){
        String email = jwtUtil.getUsername(accessToken);
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundMemberException("Not Found Member"));

        MemberDto memberDto = new MemberDto();
        memberDto.setId(member.getId());
        memberDto.setEmail(member.getEmail());
        memberDto.setNickname(member.getNickname());
        memberDto.setUnivName(member.getUnivName());

        return memberDto;
    }
}
