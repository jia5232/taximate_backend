package com.backend.kiri.service;

import com.backend.kiri.domain.Member;
import com.backend.kiri.repository.MemberRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Service
public class MemberCleanupService {
    private final MemberRepository memberRepository;

    @Scheduled(cron = "0 0 0 * * ?") // Runs every day at midnight
    public void cleanUpMembers() {
        LocalDateTime threeMonthsAgo = LocalDateTime.now().minusMonths(3);
        List<Member> membersToCleanUp = memberRepository.findByIsDeletedTrueAndCancellationDateBefore(threeMonthsAgo);

        for (Member member : membersToCleanUp) {
            member.setEmail(null);
            memberRepository.save(member);
        }
    }
}

