package com.example.swip.service;

import com.example.swip.config.JwtIssuer;
import com.example.swip.config.UserPrincipal;
import com.example.swip.dto.LoginResponse;
import com.example.swip.entity.Board;
import com.example.swip.entity.User;
import com.example.swip.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final JwtIssuer jwtIssuer;
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;
    public LoginResponse attemptLogin(String email, String password) {
        System.out.println("test : " + email + ", " + password);
        //SecurityConfig에서 구성한 SecurityFilterChain
        var authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password)
        );
        System.out.println("authentication : " + authentication.getPrincipal());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        var principal = (UserPrincipal) authentication.getPrincipal();

        var roles = principal.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        var token = jwtIssuer.issue(principal.getUserId(), principal.getEmail(), roles);
        return LoginResponse.builder()
                .accessToken(token)
                .build();
    }

    public String addUser(String email, String password) {
        System.out.println("enroll : " + email + ", " + password);
        // TODO : DB 연동 필요.
        var encode = passwordEncoder.encode(password);
        var user = User.builder()
                .password(encode)
                .email(email)
                .role("ROLE_ADMIN")
                .extraInfo("testadmin").build();
        User saveUser = userRepository.save(user);
        return "test success";
    }
}