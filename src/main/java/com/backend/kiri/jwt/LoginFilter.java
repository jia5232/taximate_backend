package com.backend.kiri.jwt;

import com.backend.kiri.domain.security.RefreshToken;
import com.backend.kiri.repository.security.RefreshTokenRepository;
import com.backend.kiri.security.CustomUserDetails;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.util.StreamUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import static org.springframework.http.HttpHeaders.CONTENT_TYPE;

// UsernamePasswordAuthenticationFilter를 상속하므로
// UsernamePasswordAuthenticationFilter의 정의에 따라 "/login"경로로 POST 요청을 검증하게 됨.
@RequiredArgsConstructor
public class LoginFilter extends UsernamePasswordAuthenticationFilter {
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JWTUtil jwtUtil;
    private final ObjectMapper objectMapper;
    private static final long ACCESS_TOKEN_TIME = 1000 * 60; // 30 분 1000ms(=1s) *60=(1min)*30 =(30min) -> 1000 * 60 * 30L
    private static final long REFRESH_TOKEN_TIME = 1000 * 60 * 5;

    // 요청에서 이메일을 가져와야 하므로 커스텀메소드 추가.
    private String obtainEmail(HttpServletRequest request){
        return request.getParameter("email");
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response){
        System.out.println("attemptAuthentication");

        try {
            // request body에서 json으로 들어온 값 읽어오기
            StringBuilder requestBody = new StringBuilder();
            BufferedReader reader = request.getReader();
            String line;
            while ((line = reader.readLine()) != null) {
                requestBody.append(line);
            }

            Map<String, String> emailPasswordMap = objectMapper.readValue(requestBody.toString(), new TypeReference<Map<String, String>>() {});

            String email = emailPasswordMap.get("email");
            String password = emailPasswordMap.get("password");
            System.out.println("email:" + email + " " + "password:" + password);

            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(email, password, null);

            return authenticationManager.authenticate(authToken);
        } catch (IOException e) {
            throw new AuthenticationServiceException("Error reading/parsing request body", e);
        }
    }

    // 로그인 성공시 실행됨 -> 여기서 jwt 발행
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication){
        System.out.println("successfulAuthentication");
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        String email = customUserDetails.getUsername();

        String accessToken = jwtUtil.createJwt(email, ACCESS_TOKEN_TIME);
        String refreshToken = jwtUtil.createJwt(email, REFRESH_TOKEN_TIME);

        RefreshToken createdRefreshToken = new RefreshToken(refreshToken, email);
        refreshTokenRepository.save(createdRefreshToken);

        response.addHeader("accessToken", "Bearer "+accessToken);
        response.addHeader("refreshToken", "Bearer "+refreshToken);
    }

    // 로그인 실패시 실행됨
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed){
        response.setStatus(401); //토큰 관련 로그인 실패 -> 401
    }
}
