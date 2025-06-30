package com.hhgs.Attendances.config;

import org.springframework.context.annotation.Configuration;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

@Configuration
@OpenAPIDefinition(info = @Info(title = "Attendance API", version = "v1", description = "API for attendance check-in and check-out"))
public class SwaggerConfig {
}
