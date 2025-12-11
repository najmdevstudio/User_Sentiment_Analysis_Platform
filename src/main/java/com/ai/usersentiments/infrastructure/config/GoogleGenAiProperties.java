package com.ai.usersentiments.infrastructure.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "google.genai")
public class GoogleGenAiProperties {
    private String apiKey;
    private String model;
    private Float temperature;
    private Float topK;
    private Float topP;
}
