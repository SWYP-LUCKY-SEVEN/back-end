package com.example.swip.config;


import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtDecoder jwtDecoder;
    private final JwtToPrincipalConverter jwtToPrincipalConverter;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        extractTokenFromRequest(request)
                .map(jwtDecoder::decode)
                .map(jwtToPrincipalConverter::convert)//UserPrincipal 반환
                .map(UserPrincipalAuthenticationToken::new)//제공된 권한 배열을 사용하여 토큰 생성
                .ifPresent(authentication -> SecurityContextHolder.getContext().setAuthentication(authentication));//현재 인증된 주체를 변경한다. 때로는 인증 정보를 제거(null일경우)한다.
        //SecurityContext 는 전역으로 어디서든 꺼낼 수 있다.
        //ifpresent Optional 객체가 값을 가지고 있으면 실행 값이 없으면 넘어감

        filterChain.doFilter(request, response); //UsernamePasswordAuthenticationFilter, username < request, password < response?
        //SessionAuthenticationStrategy 호출 (세션이 존재하는지 확인 용도)
        //성공적이면 successfulAuthentication 호출
        //성공적인 인증을 위한 기본 동작입니다.
        //성공적인 인증 개체를 설정합니다.SecurityContextHolder
        //구성된 RememberMeServices 에 성공적인 로그인을 알립니다.
        //InteractiveAuthenticationSuccessEvent구성된 ApplicationEventPublisher를 통해 실행합니다.
        //에 추가 동작을 위임합니다 AuthenticationSuccessHandler.
        //서브클래스는 이 메서드를 재정의하여 인증 성공 후 계속할 수 있습니다 FilterChain.
    }

    // Authorization: Bearer ey74823y58734.y34t897y34.y8934t8934 이런식이라서 substring 7 해서 ey 부분만 가져오기위해서
    private Optional<String> extractTokenFromRequest(HttpServletRequest request) {
        var token = request.getHeader("Authorization");
        if (StringUtils.hasText(token) && token.startsWith("Bearer ")) {
            return Optional.of(token.substring(7));
        }
        return Optional.empty();
    }
}