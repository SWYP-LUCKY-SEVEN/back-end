package com.example.swip.service.impl;

import com.example.swip.dto.DefaultResponse;
import com.example.swip.dto.study.PostStudyAddMemberRequest;
import com.example.swip.dto.study.PostStudyDeleteMemberRequest;
import com.example.swip.dto.user.PostProfileDto;
import com.example.swip.dto.user.PostProfileResponse;
import com.example.swip.dto.study.PostStudyRequest;
import com.example.swip.service.ChatServerService;
import com.mysema.commons.lang.Pair;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
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
    private String reqUserURL;
    public Pair<String, Integer> postUser(PostProfileDto profileDto){
        String jsonInputString = "{\"pk\":\""+profileDto.getUser_id().toString()
                +"\",\"nickname\":\""+profileDto.getNickname()
                +"\",\"pic\":\""+profileDto.getProfileImage()+"\"}";
        Pair<String, Integer> result = sendHttpRequest(reqUserURL, "POST", jsonInputString, null);
        return result;
    }

    public Pair<String, Integer> updateUser(PostProfileDto profileDto){
        String reqDeleteUserURL = String.format("%s/%s", reqUserURL, profileDto.getUser_id());
        String jsonInputString = "{\"nickname\":\""+profileDto.getNickname()
                +"\",\"pic\":\""+profileDto.getProfileImage()+"\"}";
        Pair<String, Integer> result = sendHttpRequest(reqDeleteUserURL, "PATCH", jsonInputString, null);
        return result;
    }
    public Pair<String, Integer> deleteUser(Long userId){
        String reqDeleteUserURL = String.format("%s/%s", reqUserURL, userId);
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
        String jsonInputString = "{\"studyId\":\""+ postStudyRequest.getStudyId().toString()
                +"\",\"pk\":\""+postStudyRequest.getPk().toString()
                +"\",\"name\":\""+postStudyRequest.getName()+"\"}";
        Pair<String, Integer> result = sendHttpRequest(studyReqURL, "POST", jsonInputString, null);

        return DefaultResponse.builder()
                .message(result.getFirst())
                .build();
    }

    @Value("${swyp.chat.server.study.add.member.uri}")
    private String studyAddMemberReqURL;
    @Override
    public DefaultResponse addStudyMember(PostStudyAddMemberRequest postStudyAddmemberRequest) {
        String bearerToken = postStudyAddmemberRequest.getToken();

        String jsonInputString = "{\"studyId\":\""+ postStudyAddmemberRequest.getStudyId().toString()
                +"\",\"userId\":\""+postStudyAddmemberRequest.getUserId().toString()
                +"\",\"type\":\""+postStudyAddmemberRequest.getType()+"\"}";
        Pair<String, Integer> result = sendHttpRequest(studyAddMemberReqURL, "PUT", jsonInputString, bearerToken);

        return DefaultResponse.builder()
                .message(result.getFirst())
                .build();
    }

    @Value("${swyp.chat.server.study.delete.member.uri}")
    private String studyDeleteMemberReqURL;
    @Override
    public DefaultResponse deleteStudyMember(PostStudyDeleteMemberRequest postStudymemberRequest) {
        String bearerToken = postStudymemberRequest.getToken();

        String jsonInputString = "{\"studyId\":\""+ postStudymemberRequest.getStudyId().toString()
                +"\",\"userId\":\""+postStudymemberRequest.getUserId().toString() +"\"}";
        Pair<String, Integer> result = sendHttpRequest(studyDeleteMemberReqURL, "PUT", jsonInputString, bearerToken);

        System.out.println("jsonInputString = " + jsonInputString);
        System.out.println("bearerToken = " + bearerToken);

        return DefaultResponse.builder()
                .message(result.getFirst())
                .build();
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
        } catch (IOException e) {
            e.printStackTrace();
        }

        return Pair.of("success!", 200);
    }
    public static Pair<String, Integer> sendRequestUserQueryParam(String reqURL, String method, Map<String, String> queryParams, String bearerToken) {
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
        return sendHttpRequest(reqURL, method, jsonInputString, bearerToken);
    }
}