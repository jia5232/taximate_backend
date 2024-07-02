package com.backend.taximate.jwt;

import com.backend.taximate.domain.Member;
import com.backend.taximate.repository.security.RefreshTokenRepository;
import com.backend.taximate.security.CustomUserDetails;
import com.backend.taximate.service.dto.security.ResponseDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
public class JWTFilter extends OncePerRequestFilter { //jwt 검증하는 필터
    private final JWTUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authorization = request.getHeader("Authorization");

        if(authorization==null || !authorization.startsWith("Bearer ")){
            System.out.println("token null");
            filterChain.doFilter(request, response);
            return;
        }

        System.out.println("authorization now");
        String token = authorization.split(" ")[1];

        try{
            if(jwtUtil.isExpired(token)){
                System.out.println("토큰 만료!!!"+jwtUtil.isExpired(token));
                throw new ExpiredJwtException(null, null, "JWT token has expired");
            }
            // 토큰에서 email(username) 획득
            String email = jwtUtil.getUsername(token);

            // member를 생성하여 값 set
            Member member = new Member();
            member.setEmail(email);

            // UserDetails에 회원 정보 객체 담기
            CustomUserDetails customUserDetails = new CustomUserDetails(member);

            // 스프링 시큐리티 인증 토큰 생성 -> customUserDetails.getAuthorities(?)
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());

            // 세션에 사용자 등록
            SecurityContextHolder.getContext().setAuthentication(authToken);
        } catch (ExpiredJwtException e){
            System.out.println("catch!:"+e);

            ObjectMapper objectMapper = new ObjectMapper();
            ResponseDto responseDto = new ResponseDto();
            responseDto.setStatus("401");
            responseDto.setMessage("토큰이 만료되었습니다.");

            response.setCharacterEncoding("utf-8");
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setStatus(HttpStatus.UNAUTHORIZED.value());

            response.getWriter().write(objectMapper.writeValueAsString(responseDto));

            return;
        }
        filterChain.doFilter(request, response);
    }
}
