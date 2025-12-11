package com.ai.usersentiments.modules.providers;

import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class AnthropicProvider implements ModelProvider{

    private final ChatModel anthropicChatModel;

    public AnthropicProvider(@Qualifier("anthropicChatModel") ChatModel chatModel) {
        this.anthropicChatModel = chatModel;
    }

    @Override
    public String name(){
        return "anthropic";
    }

    @Override
    public String generate(String prompt){
        String response = anthropicChatModel.call(prompt);
        if (response == null || response.isBlank()) {
            throw new RuntimeException("Anthropic returned null content for scoring");
        }
        return response;

    }
}
