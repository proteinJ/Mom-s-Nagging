package com.mom.nagging.domain.admin;

import com.mom.nagging.domain.member.domain.Member;
import com.mom.nagging.domain.member.repository.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class AdminService {

    private MemberRepository memberRepository;

    public List<Member> getAllMember() {
        memberRepository.findAll();
        return memberRepository.findAll();
    }
}
