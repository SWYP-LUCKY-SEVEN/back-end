package com.example.swip.service.impl;

import com.example.swip.config.HttpRequestSender;
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

@Service
@RequiredArgsConstructor
public class ChatServerServiceImpl implements ChatServerService {
    private final HttpRequestSender httpRequestSender;

    @Value("${swyp.chat.server.uri}")
    private String reqUserURL;
    public Pair<String, Integer> postUser(ChatProfileRequest chatProfileRequest){
        Pair<String, Integer> result;
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String jsonInputString = objectMapper.writeValueAsString(chatProfileRequest);
            result = httpRequestSender.sendHttpRequest(reqUserURL, "POST", jsonInputString, null);
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
            result = httpRequestSender.sendHttpRequest(reqUpdateUserURL, "PATCH", jsonInputString, null);
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
        Pair<String, Integer> result = httpRequestSender.sendHttpRequest(reqDeleteUserURL, "DELETE", null, null);
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
        Pair<String, Integer> result = httpRequestSender.sendHttpRequest(studyReqURL, "POST", jsonInputString, null);

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

        Pair<String, Integer> result = httpRequestSender.sendHttpRequest(studyAddMemberReqURL, "PUT", jsonInputString, bearerToken);
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
        Pair<String, Integer> result = httpRequestSender.sendHttpRequest(studyDeleteMemberReqURL, "PUT", jsonInputString, bearerToken);

        System.out.println("jsonInputString = " + jsonInputString);
        System.out.println("bearerToken = " + bearerToken);

        return DefaultResponse.builder()
                .message(result.getFirst())
                .build();
    }
}