package com.example.swip.service.impl;

import com.example.swip.dto.chat.ChatProfileRequest;
import com.example.swip.dto.chat.DeleteStudyRequest;
import com.example.swip.dto.chat.UpdateStudyRequest;
import com.example.swip.dto.study.PostStudyAddMemberRequest;
import com.example.swip.dto.study.PostStudyDeleteMemberRequest;
import com.example.swip.dto.study.PostStudyRequest;
import com.example.swip.dto.user.PostProfileDto;
import com.example.swip.entity.Study;
import com.example.swip.entity.User;
import com.example.swip.entity.UserStudy;
import com.example.swip.entity.compositeKey.UserStudyId;
import com.example.swip.entity.enumtype.ChatStatus;
import com.example.swip.repository.StudyRepository;
import com.example.swip.repository.UserRepository;
import com.example.swip.repository.UserStudyRepository;
import com.example.swip.service.ChatServerService;
import com.example.swip.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mysema.commons.lang.Pair;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChatServerServiceImpl implements ChatServerService {

    private final StudyRepository studyRepository;
    private final UserRepository userRepository;
    private final UserStudyRepository userStudyRepository;

    @Value("${swyp.chat.server.uri}")
    private String reqUserURL;
    @Value("${swyp.chat.server.chatRoom.url}")
    private String reqStudyURL;

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

        if(result.getSecond() != 200)
            return setUserStatusAndReturnPair(Long.valueOf(chatProfileRequest.getPk()), result.getSecond(), ChatStatus.Need_create);

        return result;
    }

    public Pair<String, Integer> updateUser(ChatProfileRequest chatProfileRequest){
        Long userId = Long.valueOf(chatProfileRequest.getPk());

        ChatStatus chatStatus = userRepository.findChat_statusById(userId);
        if (chatStatus == ChatStatus.Need_create) {
            Pair<String, Integer> response = syncUserData(userId, null);
            if(response.getSecond() != 200) {
                return response;
            }
        }

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

        if(result.getSecond() != 200)
            return setUserStatusAndReturnPair(Long.valueOf(chatProfileRequest.getPk()), result.getSecond(), ChatStatus.Need_update);

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
    @Override
    public Pair<String, Integer> postStudy(PostStudyRequest postStudyRequest) {

        Long studyId = Long.valueOf(postStudyRequest.getStudyId());
        Long userId = Long.valueOf(postStudyRequest.getPk());

        ChatStatus chatStatus = userRepository.findChat_statusById(userId);
        if (chatStatus == ChatStatus.Need_create) {
            Pair<String, Integer> response = syncUserData(userId, studyId);
            if(response.getSecond() != 200) {
                return response;
            }
        }

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

        if(result.getSecond() != 200) {
            return setStudyStatusAndReturnPair(studyId, result.getSecond(), ChatStatus.Need_create);
        }
        return result;
    }

    // TODO: 스터디 수정

    @Override
    public Pair<String, Integer> updateStudy(UpdateStudyRequest updateStudyRequest, Long userId) {

        Long studyId = Long.valueOf(updateStudyRequest.getChatId());

        ChatStatus chatStatus = studyRepository.findChat_statusById(studyId);
        if(chatStatus == ChatStatus.Need_create) {
            Pair<String, Integer> response = syncStudyData(userId, studyId);
            if(response.getSecond() != 200) {
                return response;
            }
        }

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

        if(result.getSecond() != 200) {
            return setStudyStatusAndReturnPair(studyId, result.getSecond(), ChatStatus.Need_update);
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
        Long studyId = Long.valueOf(postStudyAddmemberRequest.getStudyId());
        Long userId = Long.valueOf(postStudyAddmemberRequest.getUserId());

        ChatStatus chatStatus = studyRepository.findChat_statusById(studyId);
        if(chatStatus == ChatStatus.Need_create) {
            Pair<String, Integer> response = syncStudyData(userId, studyId);
            if(response.getSecond() != 200) {
                return response;
            }
        }

        ChatStatus chatStatus2 = userStudyRepository.findChat_statusById(new UserStudyId(userId, studyId));
        UserStudy userStudy = userStudyRepository.findUserStudyById(new UserStudyId(userId, studyId));
        if(chatStatus2 == ChatStatus.Need_delete) {
            userStudy.setChat_status(ChatStatus.Clear);
            return Pair.of("complete to Add Member", 200);
        }

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

        if(result.getSecond() != 200) {
            return setUserStudyStatusAndReturnPair(studyId, userId, result.getSecond(), ChatStatus.Need_add);
        }

        return result;
    }

    @Override
    public Pair<String, Integer> deleteStudyMember(PostStudyDeleteMemberRequest postStudymemberRequest) {
        Long studyId = Long.valueOf(postStudymemberRequest.getStudyId());
        Long userId = Long.valueOf(postStudymemberRequest.getUserId());

        ChatStatus chatStatus = studyRepository.findChat_statusById(studyId);
        if(chatStatus == ChatStatus.Need_create) {
            Pair<String, Integer> response = syncStudyData(userId, studyId);
            if(response.getSecond() != 200) {
                return response;
            }
        }

        ChatStatus chatStatus2 = userStudyRepository.findChat_statusById(new UserStudyId(userId, studyId));
        UserStudy userStudy = userStudyRepository.findUserStudyById(new UserStudyId(userId, studyId));
        if(chatStatus2 == ChatStatus.Need_add) {
            userStudy.setChat_status(ChatStatus.Clear);
            return Pair.of("complete to Add Member", 200);
        }

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

        if(result.getSecond() != 200) {
            return setUserStudyStatusAndReturnPair(studyId, userId, result.getSecond(), ChatStatus.Need_delete);
        }

        return result;
    }

    //TODO : 스터디 유저 (스스로) 삭제
    @Override
    public Pair<String, Integer> deleteStudyMemberSelf(PostStudyDeleteMemberRequest postStudyDeleteMemberRequest) {
        Long studyId = Long.valueOf(postStudyDeleteMemberRequest.getStudyId());
        Long userId = Long.valueOf(postStudyDeleteMemberRequest.getUserId());

        ChatStatus chatStatus = studyRepository.findChat_statusById(studyId);
        if(chatStatus == ChatStatus.Need_create) {
            Pair<String, Integer> response = syncStudyData(userId, studyId);
            if(response.getSecond() != 200) {
                return response;
            }
        }

        ChatStatus chatStatus2 = userStudyRepository.findChat_statusById(new UserStudyId(userId, studyId));
        UserStudy userStudy = userStudyRepository.findUserStudyById(new UserStudyId(userId, studyId));
        if(chatStatus2 == ChatStatus.Need_add) {
            userStudy.setChat_status(ChatStatus.Clear);
            return Pair.of("complete to Add Member", 200);
        }

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

        if(result.getSecond() != 200) {
            return setUserStudyStatusAndReturnPair(studyId, userId, result.getSecond(), ChatStatus.Need_delete);
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

    private Pair<String, Integer> syncUserData(Long userId, Long studyId) {
        Optional<User> user = userRepository.findById(userId);
        if(user.isPresent()) {
            PostProfileDto postProfileDto = PostProfileDto.builder()
                    .user_id(user.get().getId())
                    .nickname(user.get().getNickname())
                    .profileImage(user.get().getProfile_image())
                    .build();

            Pair<String, Integer> response = postUser(postProfileDto.toChatUserProfileDto());
            Integer status = response.getSecond();

            if(status == 200){
                return setUserStatusAndReturnPair(userId, status, ChatStatus.Clear);
            }
            else if(studyId == null){
                return setUserStatusAndReturnPair(userId, status, ChatStatus.Need_create);
            }
            else{
                return setStudyStatusAndReturnPair(studyId, status, ChatStatus.Need_create);
            }
        }
        return null;
    }

    private Pair<String, Integer> syncStudyData(Long studyId, Long userId){
        Optional<Study> study = studyRepository.findById(studyId);
        Optional<User> user = userRepository.findById(userId);
        if(study.isPresent()) {
            PostStudyRequest postStudyRequest = PostStudyRequest.builder()
                    .studyId(studyId.toString())
                    .pk(userId.toString())
                    .name(user.get().getNickname())
                    .build();

            Pair<String, Integer> response = postStudy(postStudyRequest);
            Integer status = response.getSecond();
            if(status == 200){
                return setStudyStatusAndReturnPair(userId, status, ChatStatus.Clear);
            }else{
                return setStudyStatusAndReturnPair(studyId, status, ChatStatus.Need_create);
            }
        }
        return null;
    }

    private Pair<String, Integer> syncUserStudyData(Long studyId, Long userId){
        return null;
    }

    private Pair<String, Integer> setUserStatusAndReturnPair(Long userId, Integer status_num, ChatStatus chatStatus) {

        User user = userRepository.findById(userId).orElse(null);
        if(chatStatus==ChatStatus.Need_create || chatStatus==ChatStatus.Need_update) {
            setChatStatus(user, status_num, chatStatus);
            return Pair.of("Failed to sync user data", 500);
        } else{
            setChatStatus(user, status_num, chatStatus);
            return Pair.of("Complete to sync user data", 200);
        }
    }

    @Transactional
    public void setChatStatus(Object obj, Integer status_num, ChatStatus defaultStatus) {
        if (status_num == 200)
            setUserOrStudyChatStatus(obj, ChatStatus.Clear);
        else
            setUserOrStudyChatStatus(obj, defaultStatus);
    }

    private void setUserOrStudyChatStatus(Object obj, ChatStatus status) {
        if(obj instanceof User)
            ((User) obj).setChat_status(status);
        else if (obj instanceof Study)
            ((Study) obj).setChat_status(status);
    }

    private Pair<String, Integer> setStudyStatusAndReturnPair(Long studyId, Integer status_num, ChatStatus chatStatus) {

        Study study = studyRepository.findById(studyId).orElse(null);
        if(chatStatus==ChatStatus.Need_create || chatStatus==ChatStatus.Need_update) {
            study.setChat_status(chatStatus);
            return Pair.of("Failed to sync study-chat data", 500);
        } else{
            study.setChat_status(chatStatus);
            return Pair.of("Complete to sync study-chat data", 200);
        }
    }

    private Pair<String, Integer> setUserStudyStatusAndReturnPair(Long studyId, Long userId, Integer status_num, ChatStatus chatStatus) {

        UserStudy userStudy = userStudyRepository.findUserStudyById(new UserStudyId(userId, studyId));
        if(chatStatus==ChatStatus.Need_add || chatStatus==ChatStatus.Need_update) {
            userStudy.setChat_status(chatStatus);
            return Pair.of("Failed to sync chat-Member data", 500);
        } else{
            userStudy.setChat_status(chatStatus);
            return Pair.of("Complete to sync chat-Member data", 200);
        }
    }
}