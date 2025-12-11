package com.ai.usersentiments.modules.providers;

import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class OpenAIProvider implements ModelProvider{

    private final ChatModel openAiChatModel;

    public OpenAIProvider(@Qualifier("openAiChatModel") ChatModel chatModel) {
        this.openAiChatModel = chatModel;
    }

    @Override
    public String name(){
        return "openAI";
    }

    @Override
    public String generate(String prompt){
        return openAiChatModel.call(prompt);
    }
}
