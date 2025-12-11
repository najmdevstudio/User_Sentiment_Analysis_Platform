package com.ai.usersentiments.infrastructure.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Sentiment Support AI API",
                version = "v1",
                description = "Multi-agent sentiment, ticket and query handling API"
        )
)
public class OpenApiConfig {
}
