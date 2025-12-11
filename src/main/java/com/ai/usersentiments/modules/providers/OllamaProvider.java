package com.ai.usersentiments.modules.providers;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OllamaProvider implements ModelProvider{

    private final ChatModel ollamaChatModel;

    @Override
    public String name(){
        return "Ollama";
    }

    @Override
    public String generate(String prompt){
        return ollamaChatModel.call(prompt);
    }
}
