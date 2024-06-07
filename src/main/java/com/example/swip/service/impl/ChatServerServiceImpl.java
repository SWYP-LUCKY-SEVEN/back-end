package com.example.swip.service.impl;

import com.example.swip.dto.DefaultResponse;
import com.example.swip.dto.chat.ChatProfileRequest;
import com.example.swip.dto.study.PostStudyAddMemberRequest;
import com.example.swip.dto.study.PostStudyDeleteMemberRequest;
import com.example.swip.dto.study.PostStudyRequest;
import com.example.swip.service.ChatServerService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mysema.commons.lang.Pair;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ChatServerServiceImpl implements ChatServerService {

    @Value("${swyp.chat.server.uri}")
    private String reqUserURL;
    public Pair<String, Integer> postUser(ChatProfileRequest chatProfileRequest){
        Pair<String, Integer> result;
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String jsonInputString = objectMapper.writeValueAsString(chatProfileRequest);
            result = sendHttpRequest(reqUserURL, "POST", jsonInputString, null);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return Pair.of("Failed to convert object to JSON", 500);
        }
        return result;
    }

    public Pair<String, Integer> updateUser(ChatProfileRequest chatProfileRequest){
        Pair<String, Integer> result;
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String reqUpdateUserURL = "";
            if (!reqUserURL.isEmpty())
                reqUpdateUserURL = String.format("%s/%s", reqUserURL, chatProfileRequest.getPk());
            String jsonInputString = objectMapper.writeValueAsString(chatProfileRequest);
            result = sendHttpRequest(reqUpdateUserURL, "PATCH", jsonInputString, null);
        }catch (JsonProcessingException e) {
            e.printStackTrace();
            return Pair.of("Failed to convert object to JSON", 500);
        }
        return result;
    }
    public Pair<String, Integer> deleteUser(Long userId){
        String reqDeleteUserURL = "";
        if(!reqUserURL.isEmpty())
            reqDeleteUserURL = String.format("%s/%s", reqUserURL, userId);
        Pair<String, Integer> result = sendHttpRequest(reqDeleteUserURL, "DELETE", null, null);
        return result;
    }

    /**
     * 스터디 생성/수정/삭제 -> 채팅 생성/수정/삭제
     */
    @Value("${swyp.chat.server.study.uri}")
    private String studyReqURL;
    @Override
    public DefaultResponse postStudy(PostStudyRequest postStudyRequest) {
        Pair<String, Integer> result;
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String jsonInputString = objectMapper.writeValueAsString(postStudyRequest);
            result = sendHttpRequest(studyReqURL, "POST", jsonInputString, null);

            return DefaultResponse.builder()
                    .message(result.getFirst())
                    .build();
        }catch (JsonProcessingException e) {
            e.printStackTrace();
            return DefaultResponse.builder()
                    .message("Failed to convert object to JSON")
                    .build();
        }
    }

    @Value("${swyp.chat.server.study.add.member.uri}")
    private String studyAddMemberReqURL;
    @Override
    public DefaultResponse addStudyMember(PostStudyAddMemberRequest postStudyAddmemberRequest) {
        Pair<String, Integer> result;
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String jsonInputString = objectMapper.writeValueAsString(postStudyAddmemberRequest);
            String bearerToken = postStudyAddmemberRequest.getToken();

            result = sendHttpRequest(studyAddMemberReqURL, "PUT", jsonInputString, bearerToken);
            return DefaultResponse.builder()
                    .message(result.getFirst())
                    .build();
        }catch (JsonProcessingException e) {
            e.printStackTrace();
            return DefaultResponse.builder()
                    .message("Failed to convert object to JSON")
                    .build();
        }
    }

    @Value("${swyp.chat.server.study.delete.member.uri}")
    private String studyDeleteMemberReqURL;
    @Override
    public DefaultResponse deleteStudyMember(PostStudyDeleteMemberRequest postStudymemberRequest) {
        Pair<String, Integer> result;
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String jsonInputString = objectMapper.writeValueAsString(postStudymemberRequest);
            String bearerToken = postStudymemberRequest.getToken();

            result = sendHttpRequest(studyDeleteMemberReqURL, "PUT", jsonInputString, bearerToken);
            return DefaultResponse.builder()
                    .message(result.getFirst())
                    .build();
        }catch (JsonProcessingException e) {
            e.printStackTrace();
            return DefaultResponse.builder()
                    .message("Failed to convert object to JSON")
                    .build();
        }
    }

    public static Pair<String, Integer> sendHttpRequest(String reqURL, String method, String jsonInputString, String bearerToken) {
        StringBuilder response = new StringBuilder();
        if(reqURL==null || reqURL.isEmpty())
            return Pair.of("deprecated",200);
        try {
            URL url = new URL(reqURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            // HTTP 메서드 설정
            conn.setRequestMethod(method);
            conn.setRequestProperty("Content-Type", "application/json");
            if(bearerToken != null) {
                conn.setRequestProperty("Authorization", "Bearer " + bearerToken); //Bearer Token 추가
            }
            conn.setDoOutput(true);
            conn.setConnectTimeout(3000); // 1초
            conn.setReadTimeout(3000);

            if ("POST".equals(method) || "PUT".equals(method)) {
                try (OutputStream os = conn.getOutputStream()) {
                    byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8); // JSON 문자열을 바이트 배열로 변환
                    os.write(input, 0, input.length); // 변환된 바이트 배열을 출력 스트림을 통해 전송
                }
            }

            int responseCode = conn.getResponseCode();
            System.out.println("responseCode : " + responseCode);

            if (responseCode >= 300 || responseCode < 200) {
                return Pair.of("HTTP request failed. Response code: ", responseCode);
            }

            // 요청을 통해 얻은 Response 메세지 읽어오기
            try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                String line;
                while ((line = br.readLine()) != null) {
                    response.append(line);
                }
            }
            System.out.println("response body : " + response);
            return Pair.of("success!", 200);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return Pair.of("time out", 408);
    }
    public static Pair<String, Integer> sendRequestUserQueryParam(String reqURL, String method, Map<String, String> queryParams, String bearerToken) {
        // 쿼리 파라미터 추가
        if (queryParams != null && !queryParams.isEmpty()) {
            StringBuilder query = new StringBuilder();
            for (Map.Entry<String, String> entry : queryParams.entrySet()) {
                if (query.length() > 0) {
                    query.append("&");
                }
                query.append(URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8));
                query.append("=");
                query.append(URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8));
            }
            reqURL += "?" + query.toString();
        }
        return sendHttpRequest(reqURL, method, null, bearerToken);
    }
}