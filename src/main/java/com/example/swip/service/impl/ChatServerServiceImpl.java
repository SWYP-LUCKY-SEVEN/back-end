package com.example.swip.service.impl;

import com.example.swip.dto.auth.PostProfileDto;
import com.example.swip.service.ChatServerService;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

@Service
@RequiredArgsConstructor
public class ChatServerServiceImpl implements ChatServerService {
    public boolean postUser(PostProfileDto postProfileDto){

        String reqURL = "https://short-tudy.onrender.com/api/user";

        try {
            URL url = new URL(reqURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            // POST 요청을 위해 기본값이 false인 setDoOutput을 true로 설정
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            String jsonInputString = "{\"pk\":\""+postProfileDto.getUser_id().toString()
                    +"\",\"nickname\":\""+postProfileDto.getNickname()
                    +"\",\"pic\":\""+postProfileDto.getProfileImage()+"\"}";
            // 서버로 전송할 JSON 형식의 문자열을 정의

            System.out.println("jsonInputString : " + jsonInputString);
            try(OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonInputString.getBytes("utf-8"); // JSON 문자열을 바이트 배열로 변환
                os.write(input, 0, input.length); // 변환된 바이트 배열을 출력 스트림을 통해 전송
            }

            int responseCode = conn.getResponseCode();
            System.out.println("responseCode : " + responseCode);

            if(responseCode >= 300 || responseCode < 200)
                return false;

            // 요청을 통해 얻은 JSON타입의 Response 메세지 읽어오기
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line = "";
            StringBuffer response = new StringBuffer();

            while ((line = br.readLine()) != null) {
                response.append(line);
            }
            br.close();

            System.out.println("response body : " + response);

            // Gson 라이브러리에 포함된 클래스로 JSON파싱 객체 생성
            JsonParser parser = new JsonParser();
            JsonElement element = parser.parse(response.toString());

            // test = element.getAsJsonObject().get("access_token").getAsString();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }
}
