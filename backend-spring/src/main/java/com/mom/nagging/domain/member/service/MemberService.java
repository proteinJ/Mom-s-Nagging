package com.mom.nagging.domain.member.service;

import com.mom.nagging.domain.member.domain.Member;
import com.mom.nagging.domain.member.domain.Role;
import com.mom.nagging.domain.member.domain.TokenDto;
import com.mom.nagging.domain.member.dto.MemberRequest;
import com.mom.nagging.domain.member.repository.MemberRepository;
import com.mom.nagging.domain.member.repository.RefreshTokenRepository;
import com.mom.nagging.global.error.BusinessException;
import com.mom.nagging.global.error.ErrorCode;
import com.mom.nagging.global.security.JwtProvider;
import com.mom.nagging.global.security.RefreshToken;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class MemberService {

    private final MemberRepository memberRepository;
    private final JwtProvider jwtProvider;
    private final RedisTemplate<Object, Object> redisTemplate;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenRepository refreshTokenRepository;

    /**
     * 회원가입
     */
    @Transactional
    public Long join(MemberRequest.Join req) {
        // 1. 중복 검증
        validateDuplicateMember(req);

        // 2. 비밀번호 암호화 및 엔티티 생성
        String encodedPassword = passwordEncoder.encode(req.password());

        Member member = Member.builder()
                .email(req.email())
                .password(encodedPassword)
                .nickname(req.nickname())
                .realname(req.realname())
                .role(Role.USER)
                .totalPoint(0)
                .build();

        return memberRepository.save(member).getId();
    }

    /**
     * 로그인
     */
    @Transactional
    public TokenDto login(MemberRequest.Login req) {

        try {
            UsernamePasswordAuthenticationToken token =
                    new UsernamePasswordAuthenticationToken(req.email(), req.password());

            // 토큰 인증 확인
            Authentication authentication = authenticationManager.authenticate(token);

            TokenDto tokenDto = jwtProvider.createToken(authentication);

            RefreshToken refreshToken = RefreshToken.builder()
                    .key(authentication.getName())
                    .value(tokenDto.getRefreshToken())
                    .build();

            refreshTokenRepository.save(refreshToken);

            return tokenDto;
        } catch (BadCredentialsException e) {
            // 비밀번호가 틀릴 경우
            log.warn("로그인 실패: 비밀번호 불일치 - 이메일: {}", req.email());
            throw new BusinessException(ErrorCode.INVALID_LOGIN_CREDENTIALS);
        } catch (InternalAuthenticationServiceException e) {
            // 아이디가 없는 경우
            log.warn("로그인 실패: 존재하지 않는 계정 - 이메일: {}", req.email());
            throw new BusinessException(ErrorCode.INVALID_LOGIN_CREDENTIALS);
        } catch (AuthenticationException e) {
            // 그 외 인증 관련 모든 예외
            throw new BusinessException(ErrorCode.AUTHENTICATION_FAILED);
        }

    }


    /**
     * 로그아웃
     */
    @Transactional
    public void logout(String bearerToken) {
        String accessToken = bearerToken.toLowerCase().startsWith("bearer ") ? bearerToken.substring(7) : bearerToken;

        if (!jwtProvider.validateToken(accessToken)) {
            throw new RuntimeException("잘못된 요청입니다.");
        }

        Authentication authentication = jwtProvider.getAuthentication(accessToken);

        Long expiration = jwtProvider.getExpiration(accessToken);

        // Redis에서 AccessToken을 블랙리스트에 등록
        redisTemplate.opsForValue()
                        .set(accessToken, "logout", expiration, TimeUnit.MILLISECONDS);

        // RF 삭제
        refreshTokenRepository.deleteByKey(authentication.getName());
    }

    /**
     * 비밀번호 변경
     */
    @Transactional
    public void changePassword(Member member, String bearerToken, MemberRequest.PasswordChange dto) {

        // 2. 현재 비밀번호가 맞는지 검증
        if (!passwordEncoder.matches(dto.oldPassword(), member.getPassword())) {
            log.warn("비밀번호 변경 실패: 비밀번호 불일치 - 멤버식별아이디: {}", member.getId());
            throw new BusinessException(ErrorCode.INVALID_LOGIN_CREDENTIALS);
        }

        member.updatePassword(passwordEncoder.encode(dto.newPassword()));
        logout(bearerToken);
    }



    /**
     * 유효성 검사
     */
    public void validateDuplicateMember(MemberRequest.Join req) {
        if (memberRepository.existsByEmail(req.email())) {
            throw new BusinessException(ErrorCode.EMAIL_DUPLICATION);
        } else if (memberRepository.findByNickname(req.nickname()).isPresent()) {
            throw new BusinessException(ErrorCode.NICKNAME_DUPLICATION);
        }
    }

    /**
     * 회원 조회
     */
    public List<Member> findAll() { return memberRepository.findAll(); }

    public Optional<Member> findOne(Long memberId) { return memberRepository.findById(memberId); }
}
