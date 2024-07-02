package com.backend.taximate.security;

import com.backend.taximate.domain.Member;
import com.backend.taximate.exception.exceptions.NotFoundMemberException;
import com.backend.taximate.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

// 로그인 과정에서 스프링 시큐리티 검증을 위한 UserDetails를 만들어 AuthenticationManager에 넘겨주기 위함.

@Service
@RequiredArgsConstructor
public class CustomUserDetailService implements UserDetailsService {
    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Member userData = findByEmail(email);

        if (userData.getIsDeleted()) {
            throw new UsernameNotFoundException("탈퇴한 회원입니다. 재가입 해주세요 (" + email + ")");
        }

        return new CustomUserDetails(userData);
    }

    private Member findByEmail(String email){
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundMemberException("Not Found By Email"));
    }
}
