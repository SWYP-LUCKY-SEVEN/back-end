package com.example.swip.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;


@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class S3AccessTime {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "s3_access_id")
    private Long id;

    private String strYearMonth;

    private Long putAccessTime;
    private Long getAccessTime;

    public void addPutAccessTime(){
        this.putAccessTime += 1;
    }
    public void addGetAccessTime(){
        this.getAccessTime += 1;
    }
}
