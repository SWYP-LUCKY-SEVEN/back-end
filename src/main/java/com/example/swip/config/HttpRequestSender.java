package com.example.swip.config;

import com.mysema.commons.lang.Pair;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
@Component
public class HttpRequestSender {
    public Pair<String, Integer> sendHttpRequest(String reqURL, String method, String jsonInputString, String bearerToken) {
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
            return Pair.of("success!", 200);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return Pair.of("time out", 408);
    }
    public  Pair<String, Integer> sendRequestUserQueryParam(String reqURL, String method, Map<String, String> queryParams, String bearerToken) {
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
