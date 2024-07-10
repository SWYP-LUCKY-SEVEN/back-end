package com.example.swip.service.impl;

import com.example.swip.dto.chat.ChatProfileRequest;
import com.example.swip.dto.chat.DeleteStudyRequest;
import com.example.swip.dto.chat.UpdateStudyRequest;
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
    @Value("${swyp.chat.sever.chatRoom.url}")
    private String reqStudyURL;
    @Override
    public Pair<String, Integer> postStudy(PostStudyRequest postStudyRequest) {
        Pair<String, Integer> result;
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String reqSaveStudyURL = "";
            if (!reqStudyURL.isEmpty())
                reqSaveStudyURL = String.format("%s/%s", reqStudyURL, "study");
            String jsonInputString = objectMapper.writeValueAsString(postStudyRequest);
            result = sendHttpRequest(reqSaveStudyURL, "POST", jsonInputString, null);
        }catch (JsonProcessingException e) {
            e.printStackTrace();
            return Pair.of("Failed to convert object to JSON", 500);
        }
        return result;
    }

    // TODO: 스터디 수정

    @Override
    public Pair<String, Integer> updateStudy(UpdateStudyRequest updateStudyRequest) {
        Pair<String, Integer> result;
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String reqUpdateStudyURL = "";
            if (!reqStudyURL.isEmpty())
                reqUpdateStudyURL = String.format("%s/%s", reqStudyURL, "group/name");
            String jsonInputString = objectMapper.writeValueAsString(updateStudyRequest);
            result = sendHttpRequest(reqUpdateStudyURL, "PUT", jsonInputString,  updateStudyRequest.getToken());
        }catch (JsonProcessingException e) {
            e.printStackTrace();
            return Pair.of("Failed to convert object to JSON", 500);
        }
        return result;
    }


    // TODO : 스터디 삭제
    @Override
    public Pair<String, Integer> deleteStudy(DeleteStudyRequest deleteStudyRequest) {
        Pair<String, Integer> result;
        ObjectMapper objectMapper = new ObjectMapper();
        String reqDeleteStudyURL = "";

        if (!reqStudyURL.isEmpty())
            reqDeleteStudyURL = String.format("%s/%s/%s", reqStudyURL, "group", deleteStudyRequest.getGroupId());

        result = sendHttpRequest(reqDeleteStudyURL, "PUT", null,  deleteStudyRequest.getToken());
        return result;
    }

    @Override
    public Pair<String, Integer> addStudyMember(PostStudyAddMemberRequest postStudyAddmemberRequest) {
        Pair<String, Integer> result;
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String studyAddMemberReqURL = "";
            if (!reqStudyURL.isEmpty())
                studyAddMemberReqURL = String.format("%s/%s", reqStudyURL, "group/add");
            String jsonInputString = objectMapper.writeValueAsString(postStudyAddmemberRequest);
            result = sendHttpRequest(studyAddMemberReqURL, "PUT", jsonInputString,  postStudyAddmemberRequest.getToken());

            System.out.println("studyAddMemberSelfReqURL = " + studyAddMemberReqURL);
            System.out.println("jsonInputString = " + jsonInputString);
        }catch (JsonProcessingException e) {
            e.printStackTrace();
            return Pair.of("Failed to convert object to JSON", 500);
        }
        return result;
    }

    @Override
    public Pair<String, Integer> deleteStudyMember(PostStudyDeleteMemberRequest postStudymemberRequest) {
        Pair<String, Integer> result;
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String studyDeleteMemberReqURL = "";
            if (!reqStudyURL.isEmpty())
                studyDeleteMemberReqURL = String.format("%s/%s", reqStudyURL, "group/remove");
            String jsonInputString = objectMapper.writeValueAsString(postStudymemberRequest);
            result = sendHttpRequest(studyDeleteMemberReqURL, "PUT", jsonInputString,  postStudymemberRequest.getToken());
        }catch (JsonProcessingException e) {
            e.printStackTrace();
            return Pair.of("Failed to convert object to JSON", 500);
        }
        return result;
    }

    //TODO : 스터디 유저 (스스로) 삭제
    @Override
    public Pair<String, Integer> deleteStudyMemberSelf(PostStudyDeleteMemberRequest postStudyDeleteMemberRequest) {
        Pair<String, Integer> result;
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            String studyDeleteMemberSelfReqURL = "";
            if (!reqStudyURL.isEmpty())
                studyDeleteMemberSelfReqURL = String.format("%s/%s/%s", reqStudyURL, "group/user", postStudyDeleteMemberRequest.getUserId());
            String jsonInputString = objectMapper.writeValueAsString(postStudyDeleteMemberRequest);
            result = sendHttpRequest(studyDeleteMemberSelfReqURL, "DELETE", jsonInputString,  null);

            System.out.println("studyDeleteMemberSelfReqURL = " + studyDeleteMemberSelfReqURL);
            System.out.println("jsonInputString = " + jsonInputString);
        }catch (JsonProcessingException e) {
            e.printStackTrace();
            return Pair.of("Failed to convert object to JSON", 500);
        }
        return result;
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

            if ("POST".equals(method) || "PUT".equals(method) || "DELETE".equals(method)) {
                try (OutputStream os = conn.getOutputStream()) {
                    if(jsonInputString != null) {
                        byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8); // JSON 문자열을 바이트 배열로 변환
                        os.write(input, 0, input.length); // 변환된 바이트 배열을 출력 스트림을 통해 전송
                    }
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