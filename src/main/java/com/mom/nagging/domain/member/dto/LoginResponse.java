package com.mom.nagging.domain.member.dto;

import com.mom.nagging.domain.member.domain.Member;
import com.mom.nagging.domain.member.domain.TokenDto;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LoginResponse {

    // 토큰 정보
    private String grantType;
    private String accessToken;
    private String refreshToken;
    private Long accessTokenExpiresIn;

    // 사용자 정보
    private String nickname;
    private String role;


    public static LoginResponse of(TokenDto tokenDto, Member member) {
        return LoginResponse.builder()
                .grantType(tokenDto.getGrantType())
                .accessToken(tokenDto.getAccessToken())
                .refreshToken(tokenDto.getRefreshToken())
                .accessTokenExpiresIn(tokenDto.getAccessTokenExpiresIn())
                .nickname(member.getNickname())
                .role(member.getRole().name())
                .build();
    }
}
