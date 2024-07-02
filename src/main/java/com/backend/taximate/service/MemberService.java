package com.backend.taximate.service;

import com.backend.taximate.domain.Member;
import com.backend.taximate.domain.security.RefreshToken;
import com.backend.taximate.domain.university.University;
import com.backend.taximate.exception.exceptions.*;
import com.backend.taximate.jwt.JWTUtil;
import com.backend.taximate.repository.security.RefreshTokenRepository;
import com.backend.taximate.repository.university.UniversityRepository;
import com.backend.taximate.service.dto.member.JoinDto;
import com.backend.taximate.repository.MemberRepository;
import com.backend.taximate.service.dto.member.MemberDto;
import com.backend.taximate.service.dto.member.signup.EmailSuffixDto;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

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

    private static final long ACCESS_TOKEN_TIME = 1000 * 60 * 60 * 1; // 1시간
    private static final long REFRESH_TOKEN_TIME = 1000 * 60 * 60 * 24 * 14; // 2주

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

        message.setSubject("택시메이트 회원가입을 위한 이메일 인증번호 메일입니다.");
        message.setText("인증번호는 "+authNumber+"입니다. \n택시메이트와 함께 오늘도 안전한 등하교 되세요 :)");

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

    public boolean validateEmailSuffix(EmailSuffixDto emailSuffixDto) {
        University university = universityRepository.findByName(emailSuffixDto.getUnivName())
                .orElseThrow(() -> new NotFoundUniversityException("대학교를 찾을 수 없습니다."));
        return university.getEmailSuffix().equals(getEmailSuffix(emailSuffixDto.getEmail()));
    }

    public void joinProcess(JoinDto joinDto) {
        String email = joinDto.getEmail();
        String password = joinDto.getPassword();
        String nickname = joinDto.getNickname();
        String univName = joinDto.getUnivName();
        Boolean isAccept = joinDto.getIsAccept();
        Boolean isEmailAuthenticated = joinDto.getIsEmailAuthenticated();

        University findUniversityByName = universityRepository.findByName(univName)
                .orElseThrow(() -> new NotFoundUniversityException("대학교를 찾을 수 없습니다."));

        Boolean isEmailSuffixValid = findUniversityByName.getEmailSuffix().equals(getEmailSuffix(email));
        if (!isEmailSuffixValid) {
            throw new NotEnoughInfoException("대학교와 이메일이 일치하지 않습니다. 올바른 학교 이메일을 입력하세요");
        }

        Optional<Member> optionalMember = memberRepository.findByEmail(email);
        if (optionalMember.isPresent()) {
            Member existingMember = optionalMember.get();
            if (existingMember.getIsDeleted()) {
                existingMember.setIsDeleted(false);
                existingMember.setNickname(nickname);
                existingMember.setPassword(bCryptPasswordEncoder.encode(password));
                existingMember.setUnivName(findUniversityByName.getName());
                existingMember.setCancellationDate(null);
                memberRepository.save(existingMember);
                return;
            } else {
                throw new AlreadyExistMemberException("이미 가입된 회원입니다.");
            }
        }

        Boolean isNicknameExist = memberRepository.existsByNickname(nickname);
        if (isNicknameExist) {
            throw new AlreadyExistMemberException("중복된 닉네임입니다.");
        }

        if (isAccept && isEmailAuthenticated) {
            Member newMember = new Member();
            newMember.setEmail(email);
            newMember.setPassword(bCryptPasswordEncoder.encode(password));
            newMember.setNickname(nickname);
            newMember.setUnivName(findUniversityByName.getName());
            memberRepository.save(newMember);
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

    public void deleteMember(String accessToken) {
        String email = jwtUtil.getUsername(accessToken);
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundMemberException("Not Found Member"));

        member.delete();  // 소프트 삭제 처리
        memberRepository.save(member);
    }
}
