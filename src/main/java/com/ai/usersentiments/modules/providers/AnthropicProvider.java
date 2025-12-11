package com.ai.usersentiments.modules.providers;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AnthropicProvider implements ModelProvider{

    private final ChatModel anthropicChatModel;

    @Override
    public String name(){
        return "Anthropic";
    }

    @Override
    public String generate(String prompt){
        return anthropicChatModel.call(prompt);
    }
}
