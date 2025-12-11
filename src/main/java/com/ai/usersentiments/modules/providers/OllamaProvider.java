package com.ai.usersentiments.modules.providers;

import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class OllamaProvider implements ModelProvider{

    private final ChatModel ollamaChatModel;

    public OllamaProvider(@Qualifier("ollamaChatModel") ChatModel chatModel) {
        this.ollamaChatModel = chatModel;
    }


    @Override
    public String name(){
        return "ollama";
    }

    @Override
    public String generate(String prompt){
        return ollamaChatModel.call(prompt);
    }
}
