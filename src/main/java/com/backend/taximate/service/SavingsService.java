package com.backend.taximate.service;

import com.backend.taximate.domain.Member;
import com.backend.taximate.domain.MemberPost;
import com.backend.taximate.domain.Post;
import com.backend.taximate.repository.MemberRepository;
import com.backend.taximate.jwt.JWTUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Transactional
public class SavingsService {
    private final MemberRepository memberRepository;
    private final JWTUtil jwtUtil;

    public int calculateSavings(String accessToken) {
        String email = jwtUtil.getUsername(accessToken);
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Not Found Member"));

        LocalDateTime startOfMonth = YearMonth.now().atDay(1).atStartOfDay();
        LocalDateTime endOfMonth = YearMonth.now().atEndOfMonth().atTime(23, 59, 59);

        List<MemberPost> memberPosts = member.getMemberPosts().stream()
                .filter(mp -> {
                    Post post = mp.getPost();
                    boolean isWithinMonth = post.getDepartTime().isAfter(startOfMonth) && post.getDepartTime().isBefore(endOfMonth);
                    boolean isNotDeleted = !post.getIsDeleted();
                    return isWithinMonth && isNotDeleted;
                })
                .collect(Collectors.toList());

        int totalSavings = 0;

        for (MemberPost memberPost : memberPosts) {
            Post post = memberPost.getPost();
            int totalCost = post.getCost();
            int memberCount = post.getNowMember();
            int memberPaid = totalCost / memberCount;
            int savings = totalCost - memberPaid;
            totalSavings += savings;
        }

        return totalSavings;
    }
}
