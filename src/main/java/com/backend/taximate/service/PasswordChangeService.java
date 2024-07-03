package com.backend.taximate.service;

import com.backend.taximate.domain.Member;
import com.backend.taximate.exception.exceptions.IncorrectPasswordException;
import com.backend.taximate.exception.exceptions.NotFoundMemberException;
import com.backend.taximate.jwt.JWTUtil;
import com.backend.taximate.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional
public class PasswordChangeService {
    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JWTUtil jwtUtil;

    public void changePassword(String accessToken, String currentPassword, String newPassword) {
        String email = jwtUtil.getUsername(accessToken);
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundMemberException("사용자를 찾을 수 없습니다."));

        if (!bCryptPasswordEncoder.matches(currentPassword, member.getPassword())) {
            throw new IncorrectPasswordException("현재 비밀번호가 올바르지 않습니다.");
        }

        member.setPassword(bCryptPasswordEncoder.encode(newPassword));
        memberRepository.save(member);
    }
}
