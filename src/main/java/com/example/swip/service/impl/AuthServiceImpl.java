package com.example.swip.service.impl;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.swip.config.security.JwtDecoder;
import com.example.swip.config.security.JwtIssuer;
import com.example.swip.config.security.JwtToPrincipalConverter;
import com.example.swip.config.security.UserPrincipal;
import com.example.swip.dto.auth.JwtRefreshResponse;
import com.example.swip.dto.auth.ValidateTokenResponse;
import com.example.swip.dto.oauth.KakaoRegisterDto;
import com.example.swip.dto.auth.LoginResponse;
import com.example.swip.dto.oauth.OauthKakaoResponse;
import com.example.swip.entity.DefaultImage;
import com.example.swip.entity.User;
import com.example.swip.repository.DefaultImageRepository;
import com.example.swip.repository.UserRepository;
import com.example.swip.service.AuthService;
import com.example.swip.service.RefreshTokenService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final JwtIssuer jwtIssuer;
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtDecoder jwtDecoder;
    private final JwtToPrincipalConverter jwtToPrincipalConverter;
    private final RefreshTokenService refreshTokenService;
    private final DefaultImageRepository defaultImageRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;
    public LoginResponse attemptLogin(String email, String password) {
        System.out.println("test : " + email + ", " + password);
        //SecurityConfig에서 구성한 SecurityFilterChain
        //SecurityConfig에서 UserPrincipal이 반환되도록 선언됨 securityUserDetailService
        //SecurityUserDetailService에서 Username=>email으로 변경함
        var authentication = authenticationManager.authenticate(    //Authentication을 인수로 Authentication 반환
                //디코딩에도 사용되는 AbstractuAthenticationToken에 principal, credentials 정도만 추가된 Object
                //AuthenticationManagerBuilder를 통해 생성된 authenticationManager를 동작시켜, 로그인 인증과정을 가짐
                //결과적으로 생성되는 authentication은 SecurityUserDetailService를 UserDetailService로 가졌으며,
                //passwordEncoder로 BCryptPasswordEncoder 기능을 가졌다.
                new UsernamePasswordAuthenticationToken(email, password)
        );

        // 출력 : authentication : com.example.swip.config.security.UserPrincipal@21c76df1 UserPrincipal이 이미 사용되고 있다.
        System.out.println("authentication : " + authentication.getPrincipal()); //인증 중인 주체 혹은 인증 후 인증된 주체 반환

        // 해당 부분에서 Security 허용
        SecurityContextHolder.getContext().setAuthentication(authentication); //SecurityContextHolder 인증(authentication) 갱신
        var principal = (UserPrincipal) authentication.getPrincipal(); //authentication 내 중요정보(Principal)를 변환

        //GrantedAuthority에 저장된 role List를 불러옴. 이는 JWT 토큰에도 저장되어있음.
        //결국은 우리가 입력하는 Users.role과 같은 값이 반환된다.
        var roles = principal.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();
        System.out.println("roles : " + roles);

        var token = jwtIssuer.issueAT(principal.getUserId(), principal.getEmail(), principal.getValidate(), roles);
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
                .role("USER")
                .build();

        User saveUser = userRepository.save(user);
        return "SignUp success";
    }

    @Transactional
    public OauthKakaoResponse oauthLogin(User user) {
        System.out.println("test : " + user.getEmail() + ", " + user.getValidate());

        List<String> list = new LinkedList<>(Arrays.asList(user.getRole()));

        var accessToken = jwtIssuer.issueAT(user.getId(), user.getEmail(), user.getValidate(), list);
        var refreshToken = jwtIssuer.issueRT(user.getId(), user.getEmail(), user.getValidate(), list);
        refreshTokenService.addToken(refreshToken);

        Boolean isNewUser = user.getJoin_date() == null;
        if (isNewUser) {
            user.enrollProfile(); // 첫 로그인 여부 확인을 위함.
        }

        return OauthKakaoResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .isNewUser(isNewUser)
                .profileImage(user.getProfile_image())
                .build();
    }

    public User kakaoRegisterUser(KakaoRegisterDto kakaoRegisterDto) {
        User user = userRepository.findByEmailAndValidate(kakaoRegisterDto.getEmail(), "kakao");
        if(user != null) {
            return user;
        }
        // 기본 이미지 랜덤 삽입 | 별도 메서드로 분리 필요.(테스트 API에서 사용) | 어느 class의 메서드로 둘지 고민중
        String insert_image_url = null;
        List<DefaultImage> imageList = defaultImageRepository.findAllByType("profile");
        if(imageList != null) {
            Random rand = new Random();
            insert_image_url = imageList.get(rand.nextInt(imageList.size())).getImageUrl();
        }
        user = userRepository.save(kakaoRegisterDto.toEntity(insert_image_url));
        return user;
    }

    public JwtRefreshResponse JwtRefresh(UserPrincipal principal) {
        if(!principal.getIsRefreshToken())
            return null;

        if(!refreshTokenService.isTokenValid(principal.getToken()))
            return null;

        List<String> list = principal.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        var accessToken = jwtIssuer.issueAT(principal.getUserId(), principal.getEmail(), principal.getValidate(), list);
        var refreshToken = jwtIssuer.issueRT(principal.getUserId(), principal.getEmail(), principal.getValidate(), list);

        refreshTokenService.addToken(refreshToken);
        refreshTokenService.removeToken(principal.getToken());

        return JwtRefreshResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    public Boolean JwtLogout(UserPrincipal principal) {
        if(!principal.getIsRefreshToken())
            return null;

        if(!refreshTokenService.isTokenValid(principal.getToken()))
            return null;

        refreshTokenService.removeToken(principal.getToken());

        return true;
    }

    public ValidateTokenResponse compareJWTWithId(String jwt, long user_id) {
        DecodedJWT decodedJWT = jwtDecoder.decode(jwt);
        UserPrincipal userPrincipal = jwtToPrincipalConverter.convert(decodedJWT);

        if(userPrincipal == null)
            return null;

        return ValidateTokenResponse.builder()
                .validated(userPrincipal.getUserId() == user_id)
                .build();
    }
    public List<Long> getAllUserId() {
        List<Long> ids = new ArrayList<>();
        List<User> users = userRepository.findAll();

        for(User user : users) {
            ids.add(user.getId());
        }
        return ids;
    }
}