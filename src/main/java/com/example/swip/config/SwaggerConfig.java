package com.example.swip.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

@OpenAPIDefinition(
    info = @Info(
            title = "Swip API",  // 어떤 API 명세서를 위한 Swagger 페이지인지
            description = "API documentaion for Swip project", // 설명
            version = "v1"
    ))
@Configuration
public class SwaggerConfig {



}
