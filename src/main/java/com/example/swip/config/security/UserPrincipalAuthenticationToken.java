package com.example.swip.config.security;

import org.springframework.security.authentication.AbstractAuthenticationToken;

//제공된 권한 배열을 사용하여 토큰을 만드는 AbstractAuthenticationToken 상속.
public class UserPrincipalAuthenticationToken extends AbstractAuthenticationToken {
    private final UserPrincipal principal;

    public UserPrincipalAuthenticationToken(UserPrincipal principal) {
        super(principal.getAuthorities()); //AbstractAuthenticationToken생성자 호출. 해당 JWT 토큰에 저장된 UserPrincipal 내 Autorities로 토큰 생성.
        this.principal = principal; //해당 JWT토큰에 저장된 UserPrincipal로 등록
        setAuthenticated(true); //토큰을 신뢰
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public UserPrincipal getPrincipal() {
        return principal;
    }
}