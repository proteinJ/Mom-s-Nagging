package com.mom.nagging.domain.member.repository;

import com.mom.nagging.domain.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByEmail(String email);
    boolean existsByEmail(String email);

    Optional<Member> findByNickname(String nickname);
}
