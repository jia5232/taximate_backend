package com.backend.taximate.service;

import com.backend.taximate.domain.Block;
import com.backend.taximate.domain.Member;
import com.backend.taximate.exception.exceptions.AlreadyBlockedException;
import com.backend.taximate.exception.exceptions.NotFoundMemberException;
import com.backend.taximate.jwt.JWTUtil;
import com.backend.taximate.repository.BlockRepository;
import com.backend.taximate.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional
public class BlockService {
    private final MemberRepository memberRepository;
    private final BlockRepository blockRepository;
    private final JWTUtil jwtUtil;

    public void blockMember(Long blockedMemberId, String accessToken) {
        String email = jwtUtil.getUsername(accessToken);
        Member blocker = memberRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundMemberException("Not Found Member"));

        Member blocked = memberRepository.findById(blockedMemberId)
                .orElseThrow(() -> new NotFoundMemberException("Not Found Member"));

        if (blockRepository.existsByBlockerAndBlocked(blocker, blocked)) {
            throw new AlreadyBlockedException("Member already blocked");
        }

        Block block = new Block();
        block.setBlocker(blocker);
        block.setBlocked(blocked);
        blockRepository.save(block);
    }
}
