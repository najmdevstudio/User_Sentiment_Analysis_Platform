package com.ai.usersentiments.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.google.genai.Client;

@Configuration
public class GoogleGenAiConfig {

    @Bean
    public Client googleGenAiClient(GoogleGenAiProperties props) {

        if (props.getApiKey() != null && !props.getApiKey().isBlank()) {
            return Client.builder()
                    .apiKey(props.getApiKey())
                    .build();
        }

        // Fallback to environment variable GOOGLE_API_KEY (supported by SDK)
        return new Client();
    }
}
