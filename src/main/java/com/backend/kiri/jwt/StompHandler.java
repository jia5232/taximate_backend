package com.backend.kiri.jwt;

import lombok.RequiredArgsConstructor;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 99)
public class StompHandler implements ChannelInterceptor {
    private final JWTUtil jwtUtil;

    // 웹소켓을 통해서 들어온 요청이 처리 되기 전에 실행됨
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        if (StompCommand.CONNECT == accessor.getCommand()) {
            String jwtToken = accessor.getFirstNativeHeader("Authorization");
            if (jwtToken != null && jwtToken.startsWith("Bearer ")) {
                jwtToken = jwtToken.substring(7); // "Bearer " 제거
                if (jwtUtil.isExpired(jwtToken)) {
                    throw new IllegalArgumentException("토큰이 만료되었습니다.");
                }
            } else {
                throw new IllegalArgumentException("JWT 형식이 적절하지 않습니다.");
            }
        }
        return message;
    }
}
