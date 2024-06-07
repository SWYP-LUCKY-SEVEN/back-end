package com.example.swip.service.impl;

import com.example.swip.dto.oauth.KakaoRegisterDto;
import com.example.swip.service.KakaoOauthService;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mysema.commons.lang.Pair;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;


@Service
@RequiredArgsConstructor
public class KakaoOauthServiceImpl implements KakaoOauthService {
    @Value("${spring.security.oauth2.client.registration.kakao.client-id}")
    private String apikey;
    @Value("${spring.security.oauth2.client.registration.kakao.authorization-grant-type}")
    private String grantType;
    @Value("${spring.security.oauth2.client.registration.kakao.redirect-uri}")
    private String redirectUrl;

    @Transactional
    public String getKakaoAccessToken(String code) {
        String reqURL = "https://kauth.kakao.com/oauth/token";

        StringBuilder parameters = new StringBuilder();
        parameters.append("grant_type="+grantType);
        parameters.append("&client_id="+apikey);
        parameters.append("&redirect_uri="+redirectUrl);
        parameters.append("&code="+code);

        try {
            String result = sendKakaoApiPostRequest(reqURL,
                    parameters.toString(),
                    "application/x-www-form-urlencoded",
                    null);
            JsonElement element = JsonParser.parseString(result);

            String accessToken = element.getAsJsonObject().get("access_token").getAsString();
            String refreshToken = element.getAsJsonObject().get("refresh_token").getAsString();

            return accessToken;
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    public Pair<KakaoRegisterDto, Long> getKakaoProfile(String accessToken) {
        //JAVA HTTP POST 작성
        try {
            String reqURL = "https://kapi.kakao.com/v2/user/me";
            String result = sendKakaoApiPostRequest(reqURL, "",
                    "application/x-www-form-urlencoded",
                    "Bearer "+accessToken);

            JsonElement element = JsonParser.parseString(result);

            Long id = element.getAsJsonObject().get("id").getAsLong();
            JsonElement kakaoAccount = element.getAsJsonObject().get("kakao_account");
            JsonElement properties = element.getAsJsonObject().get("properties");

            String email = kakaoAccount.getAsJsonObject().get("email").getAsString();
            String nickname = properties.getAsJsonObject().get("nickname").getAsString();

            return Pair.of(KakaoRegisterDto.builder()
                    .email(email)
                    .nickname(nickname)
                    .role("USER").build(), id);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Value("${spring.security.oauth2.client.registration.kakao.admin-key}")
    private String adminKey;
    public Long logOutKakao(Long kakaoUserId) {
        //JAVA HTTP POST 작성
        if(adminKey == null){
            throw new IllegalArgumentException("The admin key cannot be null");
        }
        String reqURL = "https://kapi.kakao.com/v1/user/logout";

        StringBuilder parameters = new StringBuilder();
        parameters.append("target_id_type=user_id");
        parameters.append("&target_id="+kakaoUserId);

        try {
            String result = sendKakaoApiPostRequest(reqURL, parameters.toString(), "application/x-www-form-urlencoded", "KakaoAK " + adminKey);

            JsonElement element = JsonParser.parseString(result);
            return element.getAsJsonObject().get("id").getAsLong();
        } catch (IOException e) {
            e.printStackTrace();
            return 0L;
        }
    }


    private String sendKakaoApiPostRequest(String reqURL, String body, String contentType, String authorization) throws IOException {
        URL url = new URL(reqURL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", contentType);
        if (authorization != null) {
            conn.setRequestProperty("Authorization", authorization);
        }
        conn.setDoOutput(true);

        try (OutputStream os = conn.getOutputStream()) {    // try 문 종료 후 자원이 닫히는걸 보장
            byte[] input = body.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        int responseCode = conn.getResponseCode();
        System.out.println("responseCode : " + responseCode);

        try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
            StringBuilder result = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                result.append(line.trim());
            }
            System.out.println("response body : " + result);
            return result.toString();
        }
    }
}
