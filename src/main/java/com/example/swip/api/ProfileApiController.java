package com.example.swip.api;

import com.example.swip.dto.ProfileResponse;
import com.example.swip.service.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
public class ProfileApiController {

    private final S3Service s3Service;
    /**
     * 생성
     */
    @PostMapping("/s3_test")
    public ProfileResponse create(@RequestPart(value = "profileImg", required = false) MultipartFile multipartFile) {
        System.out.println(multipartFile);
        s3Service.putS3Test(multipartFile);
        return null;
    }
}
