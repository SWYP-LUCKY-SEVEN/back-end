package com.example.swip.service;

import com.example.swip.config.s3.S3Uploader;
import com.example.swip.entity.S3AccessTime;
import com.example.swip.repository.S3AccessTimeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
@Slf4j
@Service
@RequiredArgsConstructor
public class S3Service {
    private final S3Uploader s3Uploader;
    private final S3AccessTimeRepository s3AccessTimeRepository;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;
    public S3AccessTime getMonthlyAccessTime() {
        Date today = new Date();
        System.out.println(today.toString());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM");
        String yearMonth = simpleDateFormat.format(today);
        System.out.println(yearMonth);

        S3AccessTime s3AccessTime = s3AccessTimeRepository.findByStrYearMonth(yearMonth);
        if(s3AccessTime == null){
            return s3AccessTimeRepository.save(S3AccessTime.builder()
                            .putAccessTime(0L)
                            .getAccessTime(0L)
                            .strYearMonth(yearMonth)
                            .build());
        }
        return s3AccessTime;
    }
    @Transactional
    public String putS3Test(MultipartFile multipartFile) {
        S3AccessTime test = getMonthlyAccessTime();
        if(test.getPutAccessTime() >= 2000L) {
            return "";
        }
        System.out.println("it's accessible");

        // S3 동작
        String fileName = "test";
        if(multipartFile != null){ // 파일 업로드한 경우에만
            System.out.println("it's running");
            try{// 파일 업로드
                fileName = s3Uploader.upload(multipartFile, "images"); // S3 버킷의 images 디렉토리 안에 저장됨
                System.out.println("fileName = " + fileName);
            }catch (IOException e){
                System.out.println(e);
                return "";
            }
        }

        //종료 후에 DB에 Access 횟수 카운트
        test.addPutAccessTime();    //@Transactional 덕분에 자동 업데이트 됨.
        return fileName;
    }
}
