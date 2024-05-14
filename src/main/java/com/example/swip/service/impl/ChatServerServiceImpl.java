package com.example.swip.service.impl;

import com.example.swip.dto.DefaultResponse;
import com.example.swip.dto.auth.PostProfileDto;
import com.example.swip.dto.auth.PostProfileResponse;
import com.example.swip.dto.study.PostStudyRequest;
import com.example.swip.dto.study.PostStudyResponse;
import com.example.swip.service.ChatServerService;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ChatServerServiceImpl implements ChatServerService {
    @Value("${swyp.chat.server.uri}")
    private String reqURL;
    public PostProfileResponse postUser(PostProfileDto postProfileDto){
        String jsonInputString = "{\"pk\":\""+postProfileDto.getUser_id().toString()
                +"\",\"nickname\":\""+postProfileDto.getNickname()
                +"\",\"pic\":\""+postProfileDto.getProfileImage()+"\"}";
        String result = sendHttpRequest(reqURL, "POST", jsonInputString);

        return PostProfileResponse.builder()
                .message(result)
                .build();
    }

    public PostProfileResponse updateUser(PostProfileDto postProfileDto){
        return null;
    }
    public DefaultResponse deleteUser(Long userId){
        sendHttpRequest(reqURL, "POST", "");
        Map<String, String> queryParams = Map.of("pk", userId.toString());
        String responseDelete = sendRequestUserQueryParam(reqURL, "DELETE", queryParams);
        return DefaultResponse.builder()
                .message(responseDelete)
                .build();
    }

    /**
     * 스터디 생성/수정/삭제 -> 채팅 생성/수정/삭제
     */
    @Value("${swyp.chat.server.study.uri}")
    private String studyReqURL;
    @Override
    public DefaultResponse postStudy(PostStudyRequest postStudyRequest) {
        String jsonInputString = "{\"studyId\":\""+ postStudyRequest.getStudyId()
                +"\",\"pk\":\""+postStudyRequest.getPk()
                +"\",\"name\":\""+postStudyRequest.getName()+"\"}";
        String result = sendHttpRequest(studyReqURL, "POST", jsonInputString);

        return DefaultResponse.builder()
                .message(result)
                .build();
    }

    public static String sendHttpRequest(String reqURL, String method, String jsonInputString) {
        StringBuilder response = new StringBuilder();
        try {
            URL url = new URL(reqURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            // HTTP 메서드 설정
            conn.setRequestMethod(method);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);
            conn.setConnectTimeout(1000); // 1초
            conn.setReadTimeout(1000);

            if ("POST".equals(method) || "PUT".equals(method)) {
                try (OutputStream os = conn.getOutputStream()) {
                    byte[] input = jsonInputString.getBytes("utf-8"); // JSON 문자열을 바이트 배열로 변환
                    os.write(input, 0, input.length); // 변환된 바이트 배열을 출력 스트림을 통해 전송
                }
            }

            int responseCode = conn.getResponseCode();
            System.out.println("responseCode : " + responseCode);

            if (responseCode >= 300 || responseCode < 200) {
                return "HTTP request failed. Response code: " + responseCode;
            }

            // 요청을 통해 얻은 Response 메세지 읽어오기
            try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                String line;
                while ((line = br.readLine()) != null) {
                    response.append(line);
                }
            }
            System.out.println("response body : " + response);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "success!";
    }
    public static String sendRequestUserQueryParam(String reqURL, String method, Map<String, String> queryParams) {
        String jsonInputString = null; // GET 요청에는 body가 없으므로 null 처리
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
        return sendHttpRequest(reqURL, method, jsonInputString);
    }
}
