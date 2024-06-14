package com.backend.kiri.service;

import com.backend.kiri.domain.Member;
import com.backend.kiri.exception.exceptions.NotFoundMemberException;
import com.backend.kiri.repository.MemberRepository;
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
public class PasswordResetService {
    private final MemberRepository memberRepository;
    private final JavaMailSender javaMailSender;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public Map<String, String> requestPasswordReset(String email) {
        if (!memberRepository.existsByEmail(email)) {
            throw new NotFoundMemberException("등록되지 않은 이메일입니다.");
        }

        SimpleMailMessage message = new SimpleMailMessage();
        int authNumber = 100000 + new SecureRandom().nextInt(900000);

        message.setTo(email);
        message.setSubject("택시메이트 비밀번호 초기화 인증번호입니다.");
        message.setText("인증번호는 " + authNumber + "입니다. \n택시메이트와 함께 오늘도 안전한 등하교 되세요 :)");

        try {
            javaMailSender.send(message);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Map<String, String> response = new HashMap<>();
        response.put("authNumber", String.valueOf(authNumber));
        return response;
    }

    public void resetPassword(String email, String newPassword) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundMemberException("사용자를 찾을 수 없습니다."));

        member.setPassword(bCryptPasswordEncoder.encode(newPassword));
        memberRepository.save(member);
    }
}

