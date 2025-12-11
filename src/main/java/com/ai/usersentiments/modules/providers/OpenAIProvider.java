package com.ai.usersentiments.modules.providers;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OpenAIProvider implements ModelProvider{

    private final ChatModel openAiChatModel;

    @Override
    public String name(){
        return "openAI";
    }

    @Override
    public String generate(String prompt){
        return openAiChatModel.call(prompt);
    }
}
