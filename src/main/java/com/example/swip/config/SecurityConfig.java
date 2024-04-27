package com.example.swip.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.ObjectPostProcessor;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.Collections;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final SecurityUserDetailService securityUserDetailService;
    private final UnauthorizedHandler unauthorizedHandler;
    CorsConfigurationSource corsConfigurationSource() {
        return request -> {
            CorsConfiguration config = new CorsConfiguration();
            config.setAllowedHeaders(Collections.singletonList("*"));
            config.setAllowedMethods(Collections.singletonList("*"));
            config.setAllowedOriginPatterns(Collections.singletonList("*"));
            config.setAllowCredentials(true);
            return config;
        };
    }

    @Bean
    public SecurityFilterChain applicationSecurity(HttpSecurity http) throws Exception {
        //JWT 보유한 요청시 처리를 위한 필터, UsernamePasswordAuthenticationFilter를 동작시키고,
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class); //JWT 인증/인가를 위한 설정
        //UsernamePasswordAuthenticationFilter.doFilter 요청이 인증을 위한 것인지, 이 필터로 처리되어야 하는지 여부를 결정하는 메서드를 호출

        http
                .cors(corsConfigurer -> corsConfigurer.configurationSource(corsConfigurationSource()))  //cors 기능 비활성화
                .csrf(AbstractHttpConfigurer::disable)  //csrf 기능 비활성화
                .securityMatcher("/**") // map current config to given resource path
                .sessionManagement(sessionManagementConfigurer
                        -> sessionManagementConfigurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .formLogin(AbstractHttpConfigurer::disable) // 기본 인증 기능 비활성화
                .exceptionHandling(e-> e.authenticationEntryPoint(unauthorizedHandler));

        http
                .authorizeHttpRequests(authorize -> authorize // 요청에 대한 권한 설정 메서드
                        .requestMatchers("/").permitAll() // / 경로 요청에 대한 권한을 설정. permitAll() 모든 사용자, 인증되지않은 사용자에게 허용
                        .requestMatchers("/swagger-ui/**", "/api-docs/**", "/swagger-resources/**").permitAll() //swagger 관련 경로 요청 모든 사용자에게 허용
                        .requestMatchers("/oauth/kakao", "/oauth/**", "/user/**").permitAll()
                        .requestMatchers("/auth/**").permitAll() // 모든 사용자에게 허용
                        .requestMatchers("/board/**").permitAll() // 모든 사용자에게 허용
                        .requestMatchers(HttpMethod.GET, "/board/{boardId}/edit").hasRole("USER") //
                        .requestMatchers("/secured/**").authenticated() // ROLE_USER 에게만 허용
                        .requestMatchers("/admin/**").hasRole("USER") // ROLE_ADMIN 에게만 허용
                        .anyRequest().authenticated() // 다른 나머지 모든 요청에 대한 권한 설정, authenticated()는 인증된 사용자에게만 허용, 로그인해야만 접근 가
                );

        return http.build();
    }
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean // 로그인 시도에 사용되는 매서드
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        //AuthenticationManagerBuilder를 불러와 userDetailsService를 securityUserDetailService로 설정
        http.getSharedObject(AuthenticationManagerBuilder.class)
        .userDetailsService(securityUserDetailService).passwordEncoder(passwordEncoder());
        //이를통해 UserPrincipal 객체를 반환하는 loadUserByUsername 메소드로 오버라이딩시킴
        //

        return http.getSharedObject(AuthenticationManagerBuilder.class).build();
    }
}