package com.ai.usersentiments.modules.providers;

import com.ai.usersentiments.infrastructure.config.GoogleGenAiProperties;
import com.google.genai.Client;
import com.google.genai.types.GenerateContentConfig;
import com.google.genai.types.GenerateContentResponse;
import org.springframework.stereotype.Component;

@Component
public class GeminiProvider implements ModelProvider{

    private final Client client;
    private final GoogleGenAiProperties props;

    public GeminiProvider(Client googleGenAiClient, GoogleGenAiProperties props) {
        this.client = googleGenAiClient;
        this.props = props;
    }

    @Override
    public String name() {
        return "gemini";
    }

    @Override
    public String generate(String prompt) {

        GenerateContentResponse response = client.models.generateContent(
                props.getModel(),
                prompt,
                genaiOptions()
        );

        return response.text();
    }

    private GenerateContentConfig genaiOptions() {
        return GenerateContentConfig.builder()
                .temperature(props.getTemperature())
                .topK(props.getTopK())
                .topP(props.getTopP())
                .build();
    }
}
