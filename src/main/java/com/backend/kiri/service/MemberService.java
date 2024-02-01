package com.backend.kiri.service;

import com.backend.kiri.domain.Member;
import com.backend.kiri.domain.security.RefreshToken;
import com.backend.kiri.exception.NotFoundRefreshTokenException;
import com.backend.kiri.jwt.JWTUtil;
import com.backend.kiri.repository.security.RefreshTokenRepository;
import com.backend.kiri.service.dto.member.JoinDto;
import com.backend.kiri.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JWTUtil jwtUtil;

    private static final long ACCESS_TOKEN_TIME = 1000 * 60 * 1; // 30 분 1000ms(=1s) *60=(1min)*30 =(30min) -> 1000 * 60 * 30L
    private static final long REFRESH_TOKEN_TIME = 1000 * 60 * 5;

    public void joinProcess(JoinDto joinDto) {
        String email = joinDto.getEmail();
        String password = joinDto.getPassword();
        String nickname = joinDto.getNickname();
        String univName = joinDto.getUnivName();
        Boolean isAccept = joinDto.getIsAccept();
        Boolean isEmailAuthenticated = joinDto.getIsEmailAuthenticated();

        Boolean isEmailExist = memberRepository.existsByEmail(email);
        Boolean isNicknameExist = memberRepository.existsByNickname(nickname);

        if (isEmailExist) {
            // 이메일 중복 에러처리
            System.out.println("이메일 중복입니다");
            return;
        }

        if (isNicknameExist) {
            // 닉네임 중복 에러처리
            System.out.println("닉네임 중복입니다");
            return;
        }

        if (isAccept && isEmailAuthenticated) {
            Member member = new Member();
            member.setEmail(email);
            member.setPassword(bCryptPasswordEncoder.encode(password));
            member.setNickname(nickname);
            member.setUnivName(univName);
            memberRepository.save(member);
        }

    }

    public Map<String, String> createNewTokens(String refreshToken) {

        RefreshToken findToken = refreshTokenRepository.findById(refreshToken)
                .orElseThrow(() -> new NotFoundRefreshTokenException("리프레시 토큰을 찾을 수 없습니다."));

        String email = findToken.getEmail();

        String newAccessToken = jwtUtil.createJwt(email, ACCESS_TOKEN_TIME);
        String newRefreshToken = jwtUtil.createJwt(email, REFRESH_TOKEN_TIME);

        HashMap<String, String> result = new HashMap<>();
        result.put("accessToken", newAccessToken);
        result.put("refreshToken", newRefreshToken);

        refreshTokenRepository.delete(findToken);
        RefreshToken createdRefreshToken = new RefreshToken(newRefreshToken, email);
        refreshTokenRepository.save(createdRefreshToken);

        // 이미 세션 생성은 JWTFilter에서 끝났으므로 다시 세션을 생성해줄 필요가 없다.

        return result;
    }
}
