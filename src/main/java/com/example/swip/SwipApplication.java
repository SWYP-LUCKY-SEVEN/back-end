package com.example.swip;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

import java.time.LocalDate;
import java.time.LocalDateTime;

@SpringBootApplication
public class SwipApplication {

	public static void main(String[] args) {
		SpringApplication.run(SwipApplication.class, args);
		System.out.println(LocalDate.now().minusDays(1));
		System.out.println(LocalDateTime.now().minusDays(1));
	}

}
