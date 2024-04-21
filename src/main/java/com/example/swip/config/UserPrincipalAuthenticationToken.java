package com.example.swip.config;

import org.springframework.security.authentication.AbstractAuthenticationToken;

//제공된 권한 배열을 사용하여 토큰을 만드는 AbstractAuthenticationToken 상속.
public class UserPrincipalAuthenticationToken extends AbstractAuthenticationToken {
    private final UserPrincipal principal;

    public UserPrincipalAuthenticationToken(UserPrincipal principal) {
        super(principal.getAuthorities()); //AbstractAuthenticationToken생성자 호출
        this.principal = principal;
        setAuthenticated(true); //토큰을 신뢰할 수 있는지 토큰을 신뢰할 수 없는지 여부
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