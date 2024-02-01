package com.backend.kiri.config;

import com.backend.kiri.jwt.JWTFilter;
import com.backend.kiri.jwt.JWTUtil;
import com.backend.kiri.jwt.LoginFilter;
import com.backend.kiri.repository.security.RefreshTokenRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    //AuthenticationManager가 인자로 받을 AuthenticationConfiguraion 객체 생성자 주입
    private final AuthenticationConfiguration authenticationConfiguration;
    private final JWTUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;
    private final ObjectMapper objectMapper;

    //AuthenticationManager Bean 등록
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception{
        return configuration.getAuthenticationManager();
    }
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // cors custom setting
        http.cors((cors) -> cors.configurationSource(apiConfigurationSource()));

        // 세션방식에서는 세션이 항상 고정되므로 csrf공격에 대해 방어해줘야 함.
        // but, jwt방식에서는 csrf에 대한 공격을 방어하지 않아도 됨.
        http.csrf((auth) -> auth.disable());

        // jwt 로그인 방식을 선택할것임.
        http.formLogin((auth) -> auth.disable());
        http.httpBasic((auth) -> auth.disable());

        // 경로별 인가 작업
        http.authorizeHttpRequests((auth) -> auth
                        .requestMatchers("/login", "/", "/signup", "/token").permitAll()
                        .anyRequest().authenticated());

        // 에러
        http.exceptionHandling((exceptionHandling) -> exceptionHandling
                .authenticationEntryPoint((request, response, authException) -> {
                    if (authException.getCause() instanceof ExpiredJwtException) {
                        System.out.println("JWT Token Expired: " + authException.getMessage());
                        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "JWT Token has expired");
                    } else {
                        System.out.println("Authentication failed: " + authException.getMessage());
                        response.sendError(HttpServletResponse.SC_FORBIDDEN, "Authentication failed");
                    }
                }));

        // LoginFilter 앞에 JWTFilter를 넣어준다.
        http.addFilterBefore(new JWTFilter(jwtUtil, refreshTokenRepository), LoginFilter.class);

        // UsernamePasswordAuthenticationFilter의 자리를 우리의 커스텀 필터로 대체한다.
        http.addFilterAt(new LoginFilter(authenticationManager(authenticationConfiguration), refreshTokenRepository, jwtUtil, objectMapper), UsernamePasswordAuthenticationFilter.class);

        // jwt방식에서는 세션을 stateless하게 관리한다.
        http.sessionManagement((session) -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }

    public CorsConfigurationSource apiConfigurationSource() {

        CorsConfiguration configuration = new CorsConfiguration();

        configuration.addAllowedOrigin("http://localhost:64700");

        configuration.addAllowedHeader("Authorization");
        configuration.addAllowedHeader("Content-Type");

        configuration.addAllowedMethod("GET");
        configuration.addAllowedMethod("POST");

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}
